package dev.siah.spintiles;

import net.minecraft.util.math.Direction;

final class SpinPlacementRules {
    private SpinPlacementRules() {
    }

    static Direction arrowFacingForPlacement(Direction playerFacing) {
        return playerFacing.getAxis().isHorizontal() ? playerFacing : Direction.NORTH;
    }
}
