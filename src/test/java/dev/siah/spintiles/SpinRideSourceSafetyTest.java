package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SpinRideSourceSafetyTest {
    @Test
    void rideClearanceDoesNotRequireFloorSupport() throws IOException {
        String source = Files.readString(Path.of("src/main/java/dev/siah/spintiles/SpinRideController.java"));

        assertTrue(source.contains("SpinTraversalRules.canTraverse"));
        assertFalse(source.contains("BlockPos below = pos.down()"));
    }

    @Test
    void rendererRotatesOnlyTheModelMatrix() throws IOException {
        Path path = Path.of("src/client/java/dev/siah/spintiles/client/mixin/PlayerEntityRendererMixin.java");
        assertTrue(Files.isRegularFile(path));

        String source = Files.readString(path);
        assertTrue(source.contains("setupTransforms"));
        assertTrue(source.contains("matrices.multiply"));
        assertFalse(source.contains("setYaw"));
        assertFalse(source.contains("headYaw ="));
        assertFalse(source.contains("bodyYaw ="));
        assertFalse(source.contains("gameRenderer"));
        assertFalse(source.contains("Camera"));
    }

    @Test
    void clientMixinConfigReferencesTheGeneratedRemapTable() throws IOException {
        String config = Files.readString(Path.of("src/main/resources/cobblemon_spin_tiles.client.mixins.json"));

        assertTrue(config.contains("\"refmap\": \"client-CobblemonSpinTiles-refmap.json\""));
    }

    @Test
    void opposingArrowsBlockBothPathScanningAndRideCollisionBypass() throws IOException {
        String controller = Files.readString(Path.of("src/main/java/dev/siah/spintiles/SpinRideController.java"));
        String block = Files.readString(Path.of("src/main/java/dev/siah/spintiles/SpinTileBlock.java"));

        assertTrue(controller.contains("SpinEntryRules.isAllowedRideTransition"));
        assertTrue(controller.contains("canBypassEntry"));
        assertTrue(block.contains("SpinRideController.canBypassEntry"));
    }

    @Test
    void blockedArrowDoesNotCreateAZeroDistanceRide() throws IOException {
        String controller = Files.readString(Path.of("src/main/java/dev/siah/spintiles/SpinRideController.java"));

        assertTrue(controller.contains("SpinPathRules.canStartRide"));
    }

    @Test
    void controllerUsesInfiniteRangeGameruleOrFiftyBlockDefault() throws IOException {
        String initializer = Files.readString(Path.of("src/main/java/dev/siah/spintiles/CobblemonSpinTiles.java"));
        String controller = Files.readString(Path.of("src/main/java/dev/siah/spintiles/SpinRideController.java"));

        assertTrue(initializer.contains("spinTilesInfiniteRange"));
        assertTrue(initializer.contains("GameRuleRegistry.register"));
        assertTrue(controller.contains("INFINITE_SPIN_TILE_RANGE"));
        assertTrue(controller.contains("SpinPathRules.UNLIMITED_SCAN_DISTANCE"));
        assertTrue(controller.contains("SpinPathRules.DEFAULT_SCAN_DISTANCE"));
    }

    @Test
    void controllerCarriesComboThroughPitchSpeedAndTileTransitions() throws IOException {
        String controller = Files.readString(Path.of("src/main/java/dev/siah/spintiles/SpinRideController.java"));

        assertTrue(controller.contains("comboCount"));
        assertTrue(controller.contains("SpinComboRules.soundPitch"));
        assertTrue(controller.contains("SpinComboRules.movementSpeed"));
        assertTrue(controller.contains("SpinComboRules.canActivateNext"));
        assertTrue(controller.contains("SUPPRESSED_START_TILES"));
    }

    @Test
    void unlimitedComboGameruleIsRegisteredAndTranslated() throws IOException {
        String initializer = Files.readString(Path.of("src/main/java/dev/siah/spintiles/CobblemonSpinTiles.java"));
        String language = Files.readString(Path.of(
                "src/main/resources/assets/cobblemon_spin_tiles/lang/en_us.json"
        ));

        assertTrue(initializer.contains("spinTilesUnlimitedCombo"));
        assertTrue(initializer.contains("UNLIMITED_SPIN_TILE_COMBO"));
        assertTrue(language.contains("gamerule.spinTilesUnlimitedCombo"));
    }
}
