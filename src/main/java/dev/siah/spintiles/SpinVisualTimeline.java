package dev.siah.spintiles;

import java.util.HashMap;
import java.util.Map;

public final class SpinVisualTimeline {
    private final Map<Integer, Long> startedAtMillis = new HashMap<>();

    public void setActive(int entityId, boolean active, long nowMillis) {
        if (active) {
            startedAtMillis.put(entityId, nowMillis);
        } else {
            startedAtMillis.remove(entityId);
        }
    }

    public boolean isActive(int entityId) {
        return startedAtMillis.containsKey(entityId);
    }

    public float rotationDegrees(int entityId, long nowMillis) {
        Long startedAt = startedAtMillis.get(entityId);
        if (startedAt == null) {
            return 0.0F;
        }
        return SpinRotationMath.degreesForElapsedMillis(nowMillis - startedAt);
    }

    public void clear() {
        startedAtMillis.clear();
    }
}
