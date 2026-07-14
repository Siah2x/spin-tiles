package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class SpinTraversalRulesTest {
    @Test
    void unsupportedAirIsTraversableWhenFeetAndHeadAreClear() {
        assertTrue(canTraverse(true, true));
    }

    @Test
    void solidBlockAtFeetStopsTheRide() {
        assertFalse(canTraverse(false, true));
    }

    @Test
    void solidBlockAtHeadStopsTheRide() {
        assertFalse(canTraverse(true, false));
    }

    private static boolean canTraverse(boolean feetClear, boolean headClear) {
        Class<?> type = assertDoesNotThrow(() -> Class.forName("dev.siah.spintiles.SpinTraversalRules"));
        Method method = assertDoesNotThrow(() -> type.getDeclaredMethod("canTraverse", boolean.class, boolean.class));
        method.setAccessible(true);
        return assertDoesNotThrow(() -> (boolean) method.invoke(null, feetClear, headClear));
    }
}
