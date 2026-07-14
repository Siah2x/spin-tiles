package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class IceTileIntegrationSourceTest {
    @Test
    void iceTileIsRegisteredAndTickedServerSide() throws IOException {
        String initializer = Files.readString(Path.of("src/main/java/dev/siah/spintiles/CobblemonSpinTiles.java"));

        assertTrue(initializer.contains("ICE_TILE"));
        assertTrue(initializer.contains("IceSlideController::tick"));
    }

    @Test
    void controllerUsesBoundingBoxCollisionAndLockedSpeed() throws IOException {
        String controller = Files.readString(Path.of("src/main/java/dev/siah/spintiles/IceSlideController.java"));

        assertTrue(controller.contains("getBoundingBox().offset"));
        assertTrue(controller.contains("isSpaceEmpty"));
        assertTrue(controller.contains("IceSlideRules.lockedVelocity"));
    }

    @Test
    void slideStartsFromObservedPlayerMovementInsteadOfServerVelocityAlone() throws IOException {
        String controller = Files.readString(Path.of("src/main/java/dev/siah/spintiles/IceSlideController.java"));

        assertTrue(controller.contains("LAST_POSITIONS"));
        assertTrue(controller.contains("currentPosition.x - previousPosition.x"));
        assertTrue(controller.contains("currentPosition.z - previousPosition.z"));
    }

    @Test
    void clientAndServerBothMaintainTheLockedSlideVelocity() throws IOException {
        String client = Files.readString(Path.of(
                "src/client/java/dev/siah/spintiles/client/CobblemonSpinTilesClient.java"
        ));
        String payload = Files.readString(Path.of("src/main/java/dev/siah/spintiles/IceSlidePayload.java"));

        assertTrue(client.contains("ClientTickEvents.END_CLIENT_TICK"));
        assertTrue(client.contains("IceSlideRules.lockedVelocity"));
        assertTrue(payload.contains("directionId"));
    }

    @Test
    void collisionProbeShrinksAwayFromTheFloorBeforeCheckingWalls() throws IOException {
        String controller = Files.readString(Path.of("src/main/java/dev/siah/spintiles/IceSlideController.java"));

        assertTrue(controller.contains("contract(COLLISION_EPSILON"));
    }

    @Test
    void clientInputLockDoesNotRotateCameraOrSuppressVerticalControls() throws IOException {
        String mixin = Files.readString(Path.of(
                "src/client/java/dev/siah/spintiles/client/mixin/KeyboardInputMixin.java"
        ));

        assertTrue(mixin.contains("movementForward = 0.0F"));
        assertTrue(mixin.contains("movementSideways = 0.0F"));
        assertFalse(mixin.contains("setYaw"));
        assertFalse(mixin.contains("setPitch"));
        assertFalse(mixin.contains("jumping = false"));
        assertFalse(mixin.contains("sneaking = false"));
    }

    @Test
    void allIceTileResourcesExist() {
        assertTrue(Files.isRegularFile(Path.of("src/main/resources/assets/cobblemon_spin_tiles/blockstates/ice_tile.json")));
        assertTrue(Files.isRegularFile(Path.of("src/main/resources/assets/cobblemon_spin_tiles/models/block/ice_tile.json")));
        assertTrue(Files.isRegularFile(Path.of("src/main/resources/assets/cobblemon_spin_tiles/models/item/ice_tile.json")));
        assertTrue(Files.isRegularFile(Path.of("src/main/resources/assets/cobblemon_spin_tiles/textures/block/ice_tile.png")));
        assertTrue(Files.isRegularFile(Path.of("src/main/resources/data/cobblemon_spin_tiles/loot_tables/blocks/ice_tile.json")));
    }
}
