package dev.siah.spintiles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

final class IceSlideController {
    private static final double FLOOR_PROBE_DEPTH = 0.05D;
    private static final double COLLISION_EPSILON = 0.001D;

    private static final Map<UUID, Direction> SLIDES = new HashMap<>();
    private static final Map<UUID, Direction> BLOCKED_DIRECTIONS = new HashMap<>();
    private static final Map<UUID, Vec3d> LAST_POSITIONS = new HashMap<>();

    private IceSlideController() {
    }

    static void tick(MinecraftServer server) {
        Set<UUID> onlinePlayers = new HashSet<>();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID playerId = player.getUuid();
            onlinePlayers.add(playerId);
            Vec3d currentPosition = player.getPos();
            Vec3d previousPosition = LAST_POSITIONS.getOrDefault(playerId, currentPosition);
            tickPlayer(player, currentPosition, previousPosition);
            LAST_POSITIONS.put(playerId, player.getPos());
        }
        SLIDES.keySet().removeIf(uuid -> !onlinePlayers.contains(uuid));
        BLOCKED_DIRECTIONS.keySet().removeIf(uuid -> !onlinePlayers.contains(uuid));
        LAST_POSITIONS.keySet().removeIf(uuid -> !onlinePlayers.contains(uuid));
    }

    private static void tickPlayer(ServerPlayerEntity player, Vec3d currentPosition, Vec3d previousPosition) {
        UUID playerId = player.getUuid();
        if (player.isRemoved() || player.isDead() || SpinRideController.isRiding(player)) {
            stop(player, false);
            BLOCKED_DIRECTIONS.remove(playerId);
            return;
        }

        boolean onIce = isStandingOnIce(player);
        if (!onIce) {
            stop(player, false);
            BLOCKED_DIRECTIONS.remove(playerId);
            return;
        }

        Direction direction = SLIDES.get(playerId);
        if (direction == null) {
            Direction candidate = IceSlideRules.chooseDirection(
                    currentPosition.x - previousPosition.x,
                    currentPosition.z - previousPosition.z
            );
            if (candidate == null) {
                Vec3d currentVelocity = player.getVelocity();
                candidate = IceSlideRules.chooseDirection(currentVelocity.x, currentVelocity.z);
            }
            if (candidate == null || candidate == BLOCKED_DIRECTIONS.get(playerId)) {
                return;
            }
            direction = candidate;
            SLIDES.put(playerId, direction);
            BLOCKED_DIRECTIONS.remove(playerId);
            IceSlideNetworking.sendState(player, true, direction);
        }

        Vec3d lockedVelocity = IceSlideRules.lockedVelocity(direction, player.getVelocity().y);
        boolean pathClear = player.getServerWorld().isSpaceEmpty(
                player,
                player.getBoundingBox().offset(lockedVelocity.x, 0.0D, lockedVelocity.z)
                        .contract(COLLISION_EPSILON)
        );
        if (!IceSlideRules.shouldContinue(true, pathClear)) {
            BLOCKED_DIRECTIONS.put(playerId, direction);
            stop(player, true);
            return;
        }

        player.setVelocity(lockedVelocity);
        player.velocityModified = true;
    }

    private static boolean isStandingOnIce(ServerPlayerEntity player) {
        BlockPos floorPos = BlockPos.ofFloored(
                player.getX(),
                player.getBoundingBox().minY - FLOOR_PROBE_DEPTH,
                player.getZ()
        );
        return player.getServerWorld().getBlockState(floorPos).getBlock() instanceof IceTileBlock;
    }

    private static void stop(ServerPlayerEntity player, boolean stopHorizontalMotion) {
        if (SLIDES.remove(player.getUuid()) == null) {
            return;
        }
        if (stopHorizontalMotion) {
            player.setVelocity(0.0D, player.getVelocity().y, 0.0D);
            player.velocityModified = true;
        }
        IceSlideNetworking.sendState(player, false, Direction.NORTH);
    }
}
