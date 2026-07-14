package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class IceSlideRulesTest {
    @Test
    void choosesDominantCardinalDirectionFromMovement() {
        assertEquals(Direction.NORTH, IceSlideRules.chooseDirection(0.08D, -0.31D));
        assertEquals(Direction.SOUTH, IceSlideRules.chooseDirection(-0.08D, 0.31D));
        assertEquals(Direction.EAST, IceSlideRules.chooseDirection(0.31D, -0.08D));
        assertEquals(Direction.WEST, IceSlideRules.chooseDirection(-0.31D, 0.08D));
    }

    @Test
    void doesNotStartWhilePlayerIsEffectivelyStationary() {
        assertNull(IceSlideRules.chooseDirection(0.01D, -0.01D));
    }

    @Test
    void startsFromARealButSmallObservedPositionChange() {
        assertEquals(Direction.NORTH, IceSlideRules.chooseDirection(0.0D, -0.02D));
    }

    @Test
    void locksHorizontalSpeedAndPreservesVerticalVelocity() {
        Vec3d velocity = IceSlideRules.lockedVelocity(Direction.NORTH, -0.17D);

        assertEquals(0.0D, velocity.x);
        assertEquals(-0.17D, velocity.y);
        assertEquals(-0.5D, velocity.z);
    }

    @Test
    void continuesOnlyWhileOnIceAndPathIsClear() {
        assertTrue(IceSlideRules.shouldContinue(true, true));
        assertFalse(IceSlideRules.shouldContinue(false, true));
        assertFalse(IceSlideRules.shouldContinue(true, false));
    }
}
