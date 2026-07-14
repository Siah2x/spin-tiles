package dev.siah.spintiles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

final class SpinRideController {
    private static final double ARRIVAL_DISTANCE_SQUARED = 0.025D;

    private static final Map<UUID, Ride> RIDES = new HashMap<>();
    private static final Map<UUID, SuppressedStart> SUPPRESSED_START_TILES = new HashMap<>();

    private SpinRideController() {
    }

    static boolean isRiding(Entity entity) {
        return entity != null && RIDES.containsKey(entity.getUuid());
    }

    static boolean canBypassEntry(Entity entity, BlockPos tilePos, Direction targetArrowFacing) {
        if (entity == null) {
            return false;
        }
        Ride ride = RIDES.get(entity.getUuid());
        if (ride == null) {
            return false;
        }
        if (tilePos.equals(ride.currentTile())) {
            return true;
        }
        return ride.resultType() == SpinPathResult.Type.CONTINUE_TO_SPIN_TILE
                && tilePos.equals(ride.target())
                && SpinEntryRules.isAllowedRideTransition(ride.direction(), targetArrowFacing);
    }

    static void tryStart(ServerWorld world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer) || isRiding(player) || !(state.getBlock() instanceof SpinTileBlock)) {
            return;
        }

        SuppressedStart suppressedStart = SUPPRESSED_START_TILES.get(serverPlayer.getUuid());
        if (suppressedStart != null) {
            if (suppressedStart.world() == world && suppressedStart.tile().equals(pos)) {
                return;
            }
            SUPPRESSED_START_TILES.remove(serverPlayer.getUuid());
        }

        Direction direction = state.get(SpinTileBlock.FACING);
        Vec3d tileCenter = Vec3d.ofCenter(pos);
        if (!SpinEntryRules.isAllowedEntry(direction, player.getPos(), tileCenter)) {
            return;
        }

        Ride ride = createRide(world, pos, direction, true, serverPlayer.getY(), 1);
        if (ride == null) {
            return;
        }
        RIDES.put(serverPlayer.getUuid(), ride);
        SpinRideNetworking.sendState(serverPlayer, true);
    }

    static void tick(MinecraftServer server) {
        SUPPRESSED_START_TILES.entrySet().removeIf(entry -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            SuppressedStart suppressedStart = entry.getValue();
            return player == null
                    || player.isRemoved()
                    || player.getServerWorld() != suppressedStart.world()
                    || !player.getBlockPos().equals(suppressedStart.tile());
        });

        Iterator<Map.Entry<UUID, Ride>> iterator = RIDES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Ride> entry = iterator.next();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null || player.isRemoved() || player.isDead()) {
                if (player != null) {
                    SpinRideNetworking.sendState(player, false);
                }
                iterator.remove();
                continue;
            }

            Ride ride = entry.getValue();
            if (player.getServerWorld().getRegistryKey() != ride.world().getRegistryKey()) {
                SpinRideNetworking.sendState(player, false);
                iterator.remove();
                continue;
            }

            Ride nextRide = advance(player, ride);
            if (nextRide == null) {
                SpinRideNetworking.sendState(player, false);
                iterator.remove();
            } else if (nextRide != ride) {
                entry.setValue(nextRide);
            }
        }
    }

    private static Ride createRide(
            ServerWorld world,
            BlockPos tilePos,
            Direction direction,
            boolean playSound,
            double y,
            int comboCount
    ) {
        int scanDistance = world.getGameRules().getBoolean(CobblemonSpinTiles.INFINITE_SPIN_TILE_RANGE)
                ? SpinPathRules.UNLIMITED_SCAN_DISTANCE
                : SpinPathRules.DEFAULT_SCAN_DISTANCE;
        SpinPathResult target = SpinPathRules.chooseTarget(
                tilePos,
                direction,
                scanDistance,
                pos -> tileKindAt(world, pos),
                pos -> isPassable(world, pos, direction)
        );
        if (!SpinPathRules.canStartRide(tilePos, target)) {
            return null;
        }
        if (playSound && SpinSoundRules.shouldPlaySpinSound(SpinTileKind.ARROW)) {
            world.playSound(
                    null,
                    tilePos,
                    CobblemonSpinTiles.SPIN_SOUND,
                    SoundCategory.BLOCKS,
                    1.0F,
                    SpinComboRules.soundPitch(comboCount)
            );
        }
        return new Ride(world, tilePos, target.target(), direction, target.type(), y, comboCount);
    }

    private static Ride advance(ServerPlayerEntity player, Ride ride) {
        Vec3d target = new Vec3d(ride.target().getX() + 0.5D, ride.y(), ride.target().getZ() + 0.5D);
        Vec3d current = player.getPos();
        Vec3d flatDelta = new Vec3d(target.x - current.x, 0.0D, target.z - current.z);
        if (flatDelta.lengthSquared() <= ARRIVAL_DISTANCE_SQUARED) {
            player.requestTeleport(target.x, ride.y(), target.z);
            player.setVelocity(0.0D, player.getVelocity().y, 0.0D);
            if (ride.resultType() == SpinPathResult.Type.CONTINUE_TO_SPIN_TILE) {
                BlockState state = ride.world().getBlockState(ride.target());
                if (state.getBlock() instanceof SpinTileBlock) {
                    boolean unlimitedCombo = ride.world().getGameRules()
                            .getBoolean(CobblemonSpinTiles.UNLIMITED_SPIN_TILE_COMBO);
                    if (!SpinComboRules.canActivateNext(ride.comboCount(), unlimitedCombo)) {
                        SUPPRESSED_START_TILES.put(
                                player.getUuid(),
                                new SuppressedStart(ride.world(), ride.target())
                        );
                        return null;
                    }
                    return createRide(
                            ride.world(),
                            ride.target(),
                            state.get(SpinTileBlock.FACING),
                            true,
                            ride.y(),
                            ride.comboCount() + 1
                    );
                }
            }
            return null;
        }

        double movementSpeed = SpinComboRules.movementSpeed(ride.comboCount());
        Vec3d step = flatDelta.normalize().multiply(Math.min(movementSpeed, Math.sqrt(flatDelta.lengthSquared())));
        player.requestTeleport(current.x + step.x, ride.y(), current.z + step.z);
        player.setVelocity(step.x, player.getVelocity().y, step.z);
        player.velocityModified = true;
        return ride;
    }

    private static SpinTileKind tileKindAt(ServerWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof SpinTileBlock) {
            return SpinTileKind.ARROW;
        }
        if (state.getBlock() instanceof SpinStopTileBlock) {
            return SpinTileKind.STOP;
        }
        return SpinTileKind.NONE;
    }

    private static boolean isPassable(ServerWorld world, BlockPos pos, Direction travelDirection) {
        if (!world.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            return false;
        }
        BlockState lowerState = world.getBlockState(pos);
        if (lowerState.getBlock() instanceof SpinTileBlock) {
            Direction targetArrowFacing = lowerState.get(SpinTileBlock.FACING);
            if (!SpinEntryRules.isAllowedRideTransition(travelDirection, targetArrowFacing)) {
                return false;
            }
        }
        boolean feetClear = lowerState.isAir()
                || lowerState.getBlock() instanceof SpinTileBlock
                || lowerState.getBlock() instanceof SpinStopTileBlock
                || lowerState.getCollisionShape(world, pos).isEmpty();
        BlockPos headPos = pos.up();
        VoxelShape headCollision = world.getBlockState(headPos).getCollisionShape(world, headPos);
        return SpinTraversalRules.canTraverse(feetClear, headCollision.isEmpty());
    }

    private record Ride(
            ServerWorld world,
            BlockPos currentTile,
            BlockPos target,
            Direction direction,
            SpinPathResult.Type resultType,
            double y,
            int comboCount
    ) {
    }

    private record SuppressedStart(ServerWorld world, BlockPos tile) {
    }
}
