package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.junit.jupiter.api.Test;

class SpinPathRulesTest {
    @Test
    void nearestSpinTileInDirectionContinuesRide() {
        BlockPos start = new BlockPos(0, 64, 0);
        SpinPathResult result = SpinPathRules.chooseTarget(
                start,
                Direction.EAST,
                12,
                pos -> pos.equals(new BlockPos(4, 64, 0)) ? SpinTileKind.ARROW : SpinTileKind.NONE,
                pos -> true
        );

        assertEquals(SpinPathResult.Type.CONTINUE_TO_SPIN_TILE, result.type());
        assertEquals(new BlockPos(4, 64, 0), result.target());
    }

    @Test
    void stopTileEndsRideBeforeLaterSpinTile() {
        BlockPos start = new BlockPos(0, 64, 0);
        SpinPathResult result = SpinPathRules.chooseTarget(
                start,
                Direction.EAST,
                12,
                pos -> {
                    if (pos.equals(new BlockPos(3, 64, 0))) {
                        return SpinTileKind.STOP;
                    }
                    if (pos.equals(new BlockPos(5, 64, 0))) {
                        return SpinTileKind.ARROW;
                    }
                    return SpinTileKind.NONE;
                },
                pos -> true
        );

        assertEquals(SpinPathResult.Type.STOP_ON_STOP_TILE, result.type());
        assertEquals(new BlockPos(3, 64, 0), result.target());
    }

    @Test
    void noTileAheadStopsOneBlockForward() {
        BlockPos start = new BlockPos(0, 64, 0);
        SpinPathResult result = SpinPathRules.chooseTarget(
                start,
                Direction.NORTH,
                12,
                pos -> SpinTileKind.NONE,
                pos -> true
        );

        assertEquals(SpinPathResult.Type.STOP_AFTER_ONE_BLOCK, result.type());
        assertEquals(new BlockPos(0, 64, -1), result.target());
    }

    @Test
    void obstructionStopsAtLastPassableBlockBeforeIt() {
        BlockPos start = new BlockPos(0, 64, 0);
        SpinPathResult result = SpinPathRules.chooseTarget(
                start,
                Direction.EAST,
                12,
                pos -> SpinTileKind.NONE,
                pos -> pos.getX() < 3
        );

        assertEquals(SpinPathResult.Type.STOP_AFTER_ONE_BLOCK, result.type());
        assertEquals(new BlockPos(1, 64, 0), result.target());
    }

    @Test
    void immediateObstructionLeavesPlayerOnTheArrow() {
        BlockPos start = new BlockPos(0, 64, 0);
        SpinPathResult result = SpinPathRules.chooseTarget(
                start,
                Direction.EAST,
                12,
                pos -> SpinTileKind.NONE,
                pos -> false
        );

        assertEquals(SpinPathResult.Type.BLOCKED_BY_OBSTRUCTION, result.type());
        assertEquals(start, result.target());
    }

    @Test
    void defaultRangeFindsTileAtFiftyButNotFiftyOne() {
        BlockPos start = new BlockPos(0, 64, 0);
        assertEquals(50, SpinPathRules.DEFAULT_SCAN_DISTANCE);

        SpinPathResult atFifty = SpinPathRules.chooseTarget(
                start,
                Direction.EAST,
                SpinPathRules.DEFAULT_SCAN_DISTANCE,
                pos -> pos.getX() == 50 ? SpinTileKind.ARROW : SpinTileKind.NONE,
                pos -> true
        );
        SpinPathResult atFiftyOne = SpinPathRules.chooseTarget(
                start,
                Direction.EAST,
                SpinPathRules.DEFAULT_SCAN_DISTANCE,
                pos -> pos.getX() == 51 ? SpinTileKind.ARROW : SpinTileKind.NONE,
                pos -> true
        );

        assertEquals(SpinPathResult.Type.CONTINUE_TO_SPIN_TILE, atFifty.type());
        assertEquals(new BlockPos(50, 64, 0), atFifty.target());
        assertEquals(SpinPathResult.Type.STOP_AFTER_ONE_BLOCK, atFiftyOne.type());
        assertEquals(new BlockPos(1, 64, 0), atFiftyOne.target());
    }

    @Test
    void unlimitedRangeCanFindTileBeyondFifty() {
        BlockPos start = new BlockPos(0, 64, 0);
        SpinPathResult result = SpinPathRules.chooseTarget(
                start,
                Direction.EAST,
                -1,
                pos -> pos.getX() == 75 ? SpinTileKind.ARROW : SpinTileKind.NONE,
                pos -> pos.getX() < 100
        );

        assertEquals(SpinPathResult.Type.CONTINUE_TO_SPIN_TILE, result.type());
        assertEquals(new BlockPos(75, 64, 0), result.target());
    }

    @Test
    void opposingArrowIsNotAConnectionAndRideStopsAfterOneBlock() {
        BlockPos start = new BlockPos(0, 64, 0);
        BlockPos opposingArrow = new BlockPos(3, 64, 0);
        SpinPathResult result = SpinPathRules.chooseTarget(
                start,
                Direction.EAST,
                12,
                pos -> pos.equals(opposingArrow) ? SpinTileKind.ARROW : SpinTileKind.NONE,
                pos -> !pos.equals(opposingArrow)
        );

        assertEquals(SpinPathResult.Type.STOP_AFTER_ONE_BLOCK, result.type());
        assertEquals(new BlockPos(1, 64, 0), result.target());
    }
}
