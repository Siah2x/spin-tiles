package dev.siah.spintiles;

final class SpinTraversalRules {
    private SpinTraversalRules() {
    }

    static boolean canTraverse(boolean feetClear, boolean headClear) {
        return feetClear && headClear;
    }
}
