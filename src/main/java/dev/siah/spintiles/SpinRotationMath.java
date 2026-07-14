package dev.siah.spintiles;

public final class SpinRotationMath {
    private static final long ROTATION_PERIOD_MILLIS = 150L;

    private SpinRotationMath() {
    }

    public static float degreesForElapsedMillis(long elapsedMillis) {
        long wrappedMillis = Math.floorMod(elapsedMillis, ROTATION_PERIOD_MILLIS);
        return wrappedMillis * 360.0F / ROTATION_PERIOD_MILLIS;
    }
}
