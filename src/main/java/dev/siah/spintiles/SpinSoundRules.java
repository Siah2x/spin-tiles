package dev.siah.spintiles;

final class SpinSoundRules {
    private SpinSoundRules() {
    }

    static boolean shouldPlaySpinSound(SpinTileKind kind) {
        return kind == SpinTileKind.ARROW;
    }
}
