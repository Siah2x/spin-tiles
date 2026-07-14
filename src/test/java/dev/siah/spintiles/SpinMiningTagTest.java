package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SpinMiningTagTest {
    @Test
    void everyModBlockIsMineableWithAnyPickaxe() throws IOException {
        Path tagPath = Path.of("src/main/resources/data/minecraft/tags/blocks/mineable/pickaxe.json");
        assertTrue(Files.isRegularFile(tagPath), "Missing vanilla pickaxe mining tag");
        String tag = Files.readString(tagPath);

        assertTrue(tag.contains("cobblemon_spin_tiles:spin_tile"));
        assertTrue(tag.contains("cobblemon_spin_tiles:spin_stop_tile"));
        assertTrue(tag.contains("cobblemon_spin_tiles:ice_tile"));
        assertTrue(tag.contains("\"replace\": false"));

        String initializer = Files.readString(Path.of(
                "src/main/java/dev/siah/spintiles/CobblemonSpinTiles.java"
        ));
        assertTrue(initializer.contains("requiresTool()"));
    }
}
