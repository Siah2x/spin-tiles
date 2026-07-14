package dev.siah.spintiles.client;

import net.minecraft.util.math.Direction;

public final class IceSlideInputState {
    private static boolean active;
    private static Direction direction = Direction.NORTH;

    private IceSlideInputState() {
    }

    public static boolean isActive() {
        return active;
    }

    public static Direction direction() {
        return direction;
    }

    static void setActive(boolean active, Direction direction) {
        IceSlideInputState.active = active;
        IceSlideInputState.direction = direction;
    }

    static void clear() {
        active = false;
        direction = Direction.NORTH;
    }
}
