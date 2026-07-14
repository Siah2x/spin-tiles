package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class SpinVisualTimelineTest {
    @Test
    void startTracksElapsedRotationAndStopRemovesIt() {
        Object timeline = newTimeline();

        invoke(timeline, "setActive", new Class<?>[]{int.class, boolean.class, long.class}, 42, true, 1_000L);
        assertTrue((boolean) invoke(timeline, "isActive", new Class<?>[]{int.class}, 42));
        assertEquals(180.0F, (float) invoke(timeline, "rotationDegrees", new Class<?>[]{int.class, long.class}, 42, 1_075L), 0.001F);

        invoke(timeline, "setActive", new Class<?>[]{int.class, boolean.class, long.class}, 42, false, 1_100L);
        assertFalse((boolean) invoke(timeline, "isActive", new Class<?>[]{int.class}, 42));
    }

    @Test
    void clearRemovesEveryActiveRider() {
        Object timeline = newTimeline();
        invoke(timeline, "setActive", new Class<?>[]{int.class, boolean.class, long.class}, 7, true, 500L);

        invoke(timeline, "clear", new Class<?>[0]);

        assertFalse((boolean) invoke(timeline, "isActive", new Class<?>[]{int.class}, 7));
    }

    private static Object newTimeline() {
        Class<?> type = assertDoesNotThrow(() -> Class.forName("dev.siah.spintiles.SpinVisualTimeline"));
        Constructor<?> constructor = assertDoesNotThrow(() -> (Constructor<?>) type.getDeclaredConstructor());
        constructor.setAccessible(true);
        return assertDoesNotThrow(() -> (Object) constructor.newInstance());
    }

    private static Object invoke(Object target, String name, Class<?>[] parameterTypes, Object... arguments) {
        Method method = assertDoesNotThrow(() -> target.getClass().getDeclaredMethod(name, parameterTypes));
        method.setAccessible(true);
        return assertDoesNotThrow(() -> method.invoke(target, arguments));
    }
}
