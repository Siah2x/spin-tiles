package dev.siah.spintiles;

final class SpinComboRules {
    static final int MAX_CHAIN_TILES = 50;

    private static final int PITCH_LEVELS = 8;
    private static final float BASE_PITCH = 1.0F;
    private static final float PITCH_STEP = 0.12F;
    private static final double BASE_SPEED = 0.32D;
    private static final double MAX_SPEED = 1.2D;
    private static final double ACCELERATION_CURVE = 18.0D;

    private SpinComboRules() {
    }

    static float soundPitch(int comboCount) {
        int pitchLevel = Math.min(PITCH_LEVELS, Math.max(1, comboCount));
        return BASE_PITCH + (pitchLevel - 1) * PITCH_STEP;
    }

    static double movementSpeed(int comboCount) {
        double completedTiles = Math.max(0, comboCount - 1);
        double progress = completedTiles / (completedTiles + ACCELERATION_CURVE);
        return BASE_SPEED + (MAX_SPEED - BASE_SPEED) * progress;
    }

    static boolean canActivateNext(int currentComboCount, boolean unlimitedCombo) {
        return unlimitedCombo || currentComboCount < MAX_CHAIN_TILES;
    }
}
