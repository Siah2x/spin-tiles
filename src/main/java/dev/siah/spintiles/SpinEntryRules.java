package dev.siah.spintiles;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

final class SpinEntryRules {
    private static final double ENTRY_SIDE_EPSILON = 0.24D;
    private static final double INSIDE_TILE_HALF_WIDTH = 0.48D;

    private SpinEntryRules() {
    }

    static boolean shouldBlockEntry(boolean riding, Direction arrowFacing, Vec3d entityPos, Vec3d tileCenter) {
        return !riding && !isAllowedEntry(arrowFacing, entityPos, tileCenter) && !isAlreadyInsideTile(entityPos, tileCenter);
    }

    static boolean isAllowedEntry(Direction arrowFacing, Vec3d entityPos, Vec3d tileCenter) {
        double dx = entityPos.x - tileCenter.x;
        double dz = entityPos.z - tileCenter.z;
        double forwardDistance = dx * arrowFacing.getOffsetX() + dz * arrowFacing.getOffsetZ();
        double lateralDistance = Math.abs(dx * arrowFacing.getOffsetZ() - dz * arrowFacing.getOffsetX());
        return forwardDistance <= ENTRY_SIDE_EPSILON || lateralDistance > forwardDistance;
    }

    static boolean isAllowedRideTransition(Direction travelDirection, Direction targetArrowFacing) {
        return targetArrowFacing != travelDirection.getOpposite();
    }

    private static boolean isAlreadyInsideTile(Vec3d entityPos, Vec3d tileCenter) {
        return Math.abs(entityPos.x - tileCenter.x) <= INSIDE_TILE_HALF_WIDTH
                && Math.abs(entityPos.z - tileCenter.z) <= INSIDE_TILE_HALF_WIDTH;
    }
}
