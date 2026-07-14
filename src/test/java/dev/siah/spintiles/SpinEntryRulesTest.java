package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class SpinEntryRulesTest {
    @Test
    void acceptsEntryFromArrowTailAndBothSides() {
        Vec3d tileCenter = new Vec3d(10.5D, 64.0D, 10.5D);

        assertTrue(SpinEntryRules.isAllowedEntry(Direction.EAST, new Vec3d(9.9D, 64.0D, 10.5D), tileCenter));
        assertTrue(SpinEntryRules.isAllowedEntry(Direction.EAST, new Vec3d(10.5D, 64.0D, 9.9D), tileCenter));
        assertTrue(SpinEntryRules.isAllowedEntry(Direction.EAST, new Vec3d(10.5D, 64.0D, 11.1D), tileCenter));
    }

    @Test
    void rejectsEntryFromArrowHeadSide() {
        Vec3d tileCenter = new Vec3d(10.5D, 64.0D, 10.5D);

        assertFalse(SpinEntryRules.isAllowedEntry(Direction.EAST, new Vec3d(11.1D, 64.0D, 10.5D), tileCenter));
    }

    @Test
    void cornersUseTheDominantApproachSide() {
        Vec3d tileCenter = new Vec3d(10.5D, 64.0D, 10.5D);

        assertTrue(SpinEntryRules.isAllowedEntry(Direction.EAST, new Vec3d(10.8D, 64.0D, 9.8D), tileCenter));
        assertFalse(SpinEntryRules.isAllowedEntry(Direction.EAST, new Vec3d(11.2D, 64.0D, 10.2D), tileCenter));
    }

    @Test
    void ridingPlayersBypassEntryBlocking() {
        assertFalse(SpinEntryRules.shouldBlockEntry(true, Direction.SOUTH, new Vec3d(10.5D, 64.0D, 9.9D), new Vec3d(10.5D, 64.0D, 10.5D)));
    }

    @Test
    void invalidEntryBlocksNonRidingPlayers() {
        assertTrue(SpinEntryRules.shouldBlockEntry(false, Direction.SOUTH, new Vec3d(10.5D, 64.0D, 11.1D), new Vec3d(10.5D, 64.0D, 10.5D)));
    }

    @Test
    void sideEntryDoesNotBlockNonRidingPlayers() {
        assertFalse(SpinEntryRules.shouldBlockEntry(false, Direction.SOUTH, new Vec3d(9.9D, 64.0D, 10.5D), new Vec3d(10.5D, 64.0D, 10.5D)));
    }

    @Test
    void rideTransitionRejectsOnlyTheOpposingArrowDirection() throws Exception {
        Method method = Arrays.stream(SpinEntryRules.class.getDeclaredMethods())
                .filter(candidate -> candidate.getName().equals("isAllowedRideTransition"))
                .findFirst()
                .orElse(null);
        assertNotNull(method, "Spin rides need an explicit opposing-arrow transition rule");
        method.setAccessible(true);

        assertFalse((boolean) method.invoke(null, Direction.EAST, Direction.WEST));
        assertTrue((boolean) method.invoke(null, Direction.EAST, Direction.EAST));
        assertTrue((boolean) method.invoke(null, Direction.EAST, Direction.NORTH));
        assertTrue((boolean) method.invoke(null, Direction.EAST, Direction.SOUTH));
    }
}
