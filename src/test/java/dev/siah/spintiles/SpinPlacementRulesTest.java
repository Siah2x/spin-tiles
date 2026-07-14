package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.math.Direction;
import org.junit.jupiter.api.Test;

class SpinPlacementRulesTest {
    @Test
    void arrowFacesTheDirectionThePlayerFacesWhenPlaced() {
        assertEquals(Direction.NORTH, SpinPlacementRules.arrowFacingForPlacement(Direction.NORTH));
        assertEquals(Direction.EAST, SpinPlacementRules.arrowFacingForPlacement(Direction.EAST));
        assertEquals(Direction.SOUTH, SpinPlacementRules.arrowFacingForPlacement(Direction.SOUTH));
        assertEquals(Direction.WEST, SpinPlacementRules.arrowFacingForPlacement(Direction.WEST));
    }

    @Test
    void verticalPlacementDirectionsFallBackToNorth() {
        assertEquals(Direction.NORTH, SpinPlacementRules.arrowFacingForPlacement(Direction.UP));
        assertEquals(Direction.NORTH, SpinPlacementRules.arrowFacingForPlacement(Direction.DOWN));
    }
}
