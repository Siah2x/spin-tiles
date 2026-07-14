package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class SpinStopTileModelTest {
    @Test
    void stopPlateModelUsesTheFullCenteredTileArea() throws IOException {
        String model = readResource("/assets/cobblemon_spin_tiles/models/block/spin_stop_tile.json")
                .replaceAll("\\s+", "");

        assertTrue(model.contains("\"from\":[0,0,0]"));
        assertTrue(model.contains("\"to\":[16,1,16]"));
    }

    @Test
    void stopPlateTextureHasASquareGrayBorderAndCenteredInterior() throws IOException {
        BufferedImage image;
        try (InputStream input = SpinStopTileModelTest.class.getResourceAsStream(
                "/assets/cobblemon_spin_tiles/textures/block/spin_stop.png"
        )) {
            if (input == null) {
                throw new IOException("Missing stop plate texture");
            }
            image = ImageIO.read(input);
        }

        assertEquals(16, image.getWidth());
        assertEquals(16, image.getHeight());
        int borderColor = image.getRGB(0, 0);
        for (int coordinate = 0; coordinate < 16; coordinate++) {
            assertEquals(borderColor, image.getRGB(coordinate, 0));
            assertEquals(borderColor, image.getRGB(coordinate, 15));
            assertEquals(borderColor, image.getRGB(0, coordinate));
            assertEquals(borderColor, image.getRGB(15, coordinate));
        }
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                assertEquals(image.getRGB(x, y), image.getRGB(15 - x, y));
                assertEquals(image.getRGB(x, y), image.getRGB(x, 15 - y));
            }
        }
    }

    private static String readResource(String path) throws IOException {
        try (InputStream input = SpinStopTileModelTest.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IOException("Missing test resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
