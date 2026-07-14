package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class LicenseMetadataTest {
    @Test
    void creditsSiahSneakaConsistently() throws IOException {
        String license = Files.readString(Path.of("LICENSE.txt"));
        String notices = Files.readString(Path.of("THIRD_PARTY_NOTICES.txt"));
        String metadata = Files.readString(Path.of("src/main/resources/fabric.mod.json"));

        assertTrue(license.contains("Copyright (c) 2026 SiahSneaka"));
        assertTrue(license.startsWith("COBBLEMON SPIN TILES LICENSE"));
        assertFalse(license.contains("SIMPLE LICENSE"));
        assertFalse(license.contains("little project"));
        assertTrue(notices.contains("claimed by SiahSneaka"));
        assertTrue(metadata.contains("\"SiahSneaka\""));
        assertFalse(metadata.contains("\"Siah\""));
    }
}
