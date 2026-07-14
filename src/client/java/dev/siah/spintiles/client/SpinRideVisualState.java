package dev.siah.spintiles.client;

import dev.siah.spintiles.SpinVisualTimeline;
import net.minecraft.util.Util;

public final class SpinRideVisualState {
    private static final SpinVisualTimeline TIMELINE = new SpinVisualTimeline();

    private SpinRideVisualState() {
    }

    static void setActive(int entityId, boolean active) {
        TIMELINE.setActive(entityId, active, Util.getMeasuringTimeMs());
    }

    public static boolean isActive(int entityId) {
        return TIMELINE.isActive(entityId);
    }

    public static float rotationDegrees(int entityId) {
        return TIMELINE.rotationDegrees(entityId, Util.getMeasuringTimeMs());
    }

    static void clear() {
        TIMELINE.clear();
    }
}
