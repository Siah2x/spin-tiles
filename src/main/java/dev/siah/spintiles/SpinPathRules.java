package dev.siah.spintiles;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

final class SpinPathRules {
    static final int DEFAULT_SCAN_DISTANCE = 50;
    static final int UNLIMITED_SCAN_DISTANCE = -1;

    private SpinPathRules() {
    }

    static SpinPathResult chooseTarget(
            BlockPos start,
            Direction direction,
            int maxDistance,
            Function<BlockPos, SpinTileKind> tileAt,
            Predicate<BlockPos> passable
    ) {
        BlockPos oneForward = start.offset(direction);
        int scanDistance = maxDistance == UNLIMITED_SCAN_DISTANCE
                ? Integer.MAX_VALUE
                : Math.max(1, maxDistance);

        for (int step = 1; step <= scanDistance; step++) {
            BlockPos candidate = start.offset(direction, step);
            if (!passable.test(candidate)) {
                if (step == 1) {
                    return new SpinPathResult(SpinPathResult.Type.BLOCKED_BY_OBSTRUCTION, start);
                }
                return new SpinPathResult(SpinPathResult.Type.STOP_AFTER_ONE_BLOCK, oneForward);
            }

            SpinTileKind kind = tileAt.apply(candidate);
            if (kind == SpinTileKind.ARROW) {
                return new SpinPathResult(SpinPathResult.Type.CONTINUE_TO_SPIN_TILE, candidate);
            }
            if (kind == SpinTileKind.STOP) {
                return new SpinPathResult(SpinPathResult.Type.STOP_ON_STOP_TILE, candidate);
            }
        }

        return new SpinPathResult(SpinPathResult.Type.STOP_AFTER_ONE_BLOCK, oneForward);
    }

    static boolean canStartRide(BlockPos start, SpinPathResult result) {
        return !start.equals(result.target());
    }
}
