package dev.siah.spintiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SpinRecipeTest {
    private static final Path RECIPE_DIR = Path.of(
            "src/main/resources/data/cobblemon_spin_tiles/recipe"
    );

    @Test
    void spinTileUsesIronBorderAndDiamondBoots() throws IOException {
        JsonObject recipe = recipe("spin_tile.json");

        assertBorderPattern(recipe, "I", "B");
        assertEquals("minecraft:iron_ingot", key(recipe, "I").get("item").getAsString());
        assertEquals("minecraft:diamond_boots", key(recipe, "B").get("item").getAsString());
        assertResult(recipe, "cobblemon_spin_tiles:spin_tile");
    }

    @Test
    void stopTileUsesGoldBorderAndAnySlab() throws IOException {
        JsonObject recipe = recipe("spin_stop_tile.json");

        assertBorderPattern(recipe, "G", "S");
        assertEquals("minecraft:gold_ingot", key(recipe, "G").get("item").getAsString());
        assertEquals("minecraft:slabs", key(recipe, "S").get("tag").getAsString());
        assertResult(recipe, "cobblemon_spin_tiles:spin_stop_tile");
    }

    @Test
    void iceTileUsesIceBorderAndAnySlab() throws IOException {
        JsonObject recipe = recipe("ice_tile.json");

        assertBorderPattern(recipe, "I", "S");
        assertEquals("minecraft:ice", key(recipe, "I").get("item").getAsString());
        assertEquals("minecraft:slabs", key(recipe, "S").get("tag").getAsString());
        assertResult(recipe, "cobblemon_spin_tiles:ice_tile");
    }

    private static JsonObject recipe(String fileName) throws IOException {
        return JsonParser.parseString(Files.readString(RECIPE_DIR.resolve(fileName))).getAsJsonObject();
    }

    private static void assertBorderPattern(JsonObject recipe, String border, String center) {
        assertEquals("minecraft:crafting_shaped", recipe.get("type").getAsString());
        JsonArray pattern = recipe.getAsJsonArray("pattern");
        assertEquals(border.repeat(3), pattern.get(0).getAsString());
        assertEquals(border + center + border, pattern.get(1).getAsString());
        assertEquals(border.repeat(3), pattern.get(2).getAsString());
    }

    private static JsonObject key(JsonObject recipe, String symbol) {
        return recipe.getAsJsonObject("key").getAsJsonObject(symbol);
    }

    private static void assertResult(JsonObject recipe, String itemId) {
        JsonObject result = recipe.getAsJsonObject("result");
        assertEquals(itemId, result.get("id").getAsString());
        assertEquals(1, result.get("count").getAsInt());
    }
}
