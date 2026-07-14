package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SpinSoundRulesTest {
    @Test
    void soundPlaysOnlyWhenEnteringArrowTile() {
        assertTrue(SpinSoundRules.shouldPlaySpinSound(SpinTileKind.ARROW));
        assertFalse(SpinSoundRules.shouldPlaySpinSound(SpinTileKind.STOP));
        assertFalse(SpinSoundRules.shouldPlaySpinSound(SpinTileKind.NONE));
    }
}
