package dev.siah.spintiles;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class IceSlideRules {
    public static final double SPEED_BLOCKS_PER_TICK = 0.5D;
    private static final double MIN_START_SPEED_SQUARED = 0.0003D;

    private IceSlideRules() {
    }

    static Direction chooseDirection(double velocityX, double velocityZ) {
        if (velocityX * velocityX + velocityZ * velocityZ < MIN_START_SPEED_SQUARED) {
            return null;
        }
        if (Math.abs(velocityX) >= Math.abs(velocityZ)) {
            return velocityX >= 0.0D ? Direction.EAST : Direction.WEST;
        }
        return velocityZ >= 0.0D ? Direction.SOUTH : Direction.NORTH;
    }

    public static Vec3d lockedVelocity(Direction direction, double verticalVelocity) {
        return new Vec3d(
                direction.getOffsetX() * SPEED_BLOCKS_PER_TICK,
                verticalVelocity,
                direction.getOffsetZ() * SPEED_BLOCKS_PER_TICK
        );
    }

    static boolean shouldContinue(boolean onIce, boolean pathClear) {
        return onIce && pathClear;
    }
}
