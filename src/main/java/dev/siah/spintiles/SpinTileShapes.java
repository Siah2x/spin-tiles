package dev.siah.spintiles;

import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;

final class SpinTileShapes {
    static final VoxelShape THIN = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    static final VoxelShape STOP_THIN = THIN;
    static final VoxelShape TWO_BLOCK_BARRIER = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);

    private SpinTileShapes() {
    }
}
