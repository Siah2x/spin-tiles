package dev.siah.spintiles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

final class SpinTileSupport {
    private SpinTileSupport() {
    }

    static boolean canPlaceAt(WorldView world, BlockPos pos) {
        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);
        return Block.isFaceFullSquare(belowState.getCollisionShape(world, below), Direction.UP);
    }
}
