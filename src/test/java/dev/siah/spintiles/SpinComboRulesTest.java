package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SpinComboRulesTest {
    @Test
    void soundUsesEightIncreasingPitchLevelsThenCaps() throws Exception {
        Method pitchMethod = method("soundPitch", int.class);
        Set<Float> pitches = new HashSet<>();
        float previous = 0.0F;

        for (int combo = 1; combo <= 8; combo++) {
            float pitch = (float) pitchMethod.invoke(null, combo);
            assertTrue(pitch > previous);
            pitches.add(pitch);
            previous = pitch;
        }

        assertEquals(8, pitches.size());
        assertEquals(previous, (float) pitchMethod.invoke(null, 9));
        assertEquals(previous, (float) pitchMethod.invoke(null, 50));
    }

    @Test
    void movementSpeedIncreasesForEveryNormalComboStep() throws Exception {
        Method speedMethod = method("movementSpeed", int.class);
        double previous = (double) speedMethod.invoke(null, 1);

        for (int combo = 2; combo <= 50; combo++) {
            double speed = (double) speedMethod.invoke(null, combo);
            assertTrue(speed > previous, "Combo " + combo + " must be faster than combo " + (combo - 1));
            previous = speed;
        }

        assertTrue(previous < 1.2D, "Normal capped chains should remain controllable");
    }

    @Test
    void chainStopsAtFiftyUnlessUncapped() throws Exception {
        Method canActivateNext = method("canActivateNext", int.class, boolean.class);

        assertTrue((boolean) canActivateNext.invoke(null, 49, false));
        assertFalse((boolean) canActivateNext.invoke(null, 50, false));
        assertTrue((boolean) canActivateNext.invoke(null, 50, true));
        assertTrue((boolean) canActivateNext.invoke(null, 500, true));
    }

    private static Method method(String name, Class<?>... parameterTypes) {
        Class<?> type = assertDoesNotThrow(() -> Class.forName("dev.siah.spintiles.SpinComboRules"));
        return assertDoesNotThrow(() -> {
            Method method = type.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        });
    }
}
