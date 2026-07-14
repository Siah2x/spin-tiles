package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class SpinRotationMathTest {
    @Test
    void spinCompletesOneFullTurnEveryOneHundredFiftyMilliseconds() {
        assertEquals(0.0F, degrees(0L), 0.001F);
        assertEquals(180.0F, degrees(75L), 0.001F);
        assertEquals(0.0F, degrees(150L), 0.001F);
        assertEquals(90.0F, degrees(187L) % 360.0F, 2.5F);
    }

    private static float degrees(long elapsedMillis) {
        Class<?> type = assertDoesNotThrow(() -> Class.forName("dev.siah.spintiles.SpinRotationMath"));
        Method method = assertDoesNotThrow(() -> type.getDeclaredMethod("degreesForElapsedMillis", long.class));
        method.setAccessible(true);
        return assertDoesNotThrow(() -> (float) method.invoke(null, elapsedMillis));
    }
}
