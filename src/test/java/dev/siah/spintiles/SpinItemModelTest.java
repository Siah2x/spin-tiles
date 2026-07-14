package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class SpinItemModelTest {
    @Test
    void arrowAndStopItemsUseModerateTopFacingHandTransforms() throws IOException {
        assertModerateHandTransforms("/assets/cobblemon_spin_tiles/models/item/spin_tile.json");
        assertModerateHandTransforms("/assets/cobblemon_spin_tiles/models/item/spin_stop_tile.json");
    }

    private static void assertModerateHandTransforms(String path) throws IOException {
        String model = readResource(path).replaceAll("\\s+", "");

        assertTrue(model.contains("\"firstperson_righthand\""));
        assertTrue(model.contains("\"firstperson_lefthand\""));
        assertTrue(model.contains("\"thirdperson_righthand\""));
        assertTrue(model.contains("\"thirdperson_lefthand\""));
        assertTrue(model.contains("\"scale\":[0.65,0.65,0.65]"));
        assertTrue(model.contains("\"scale\":[0.5,0.5,0.5]"));
    }

    private static String readResource(String path) throws IOException {
        try (InputStream input = SpinItemModelTest.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IOException("Missing item model: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
