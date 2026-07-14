package dev.siah.spintiles;

import net.minecraft.util.math.BlockPos;

record SpinPathResult(SpinPathResult.Type type, BlockPos target) {
    enum Type {
        CONTINUE_TO_SPIN_TILE,
        STOP_ON_STOP_TILE,
        STOP_AFTER_ONE_BLOCK,
        BLOCKED_BY_OBSTRUCTION
    }
}
