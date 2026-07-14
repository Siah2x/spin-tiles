package dev.siah.spintiles;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public final class CobblemonSpinTiles implements ModInitializer {
    public static final String MOD_ID = "cobblemon_spin_tiles";

    public static final GameRules.Key<GameRules.BooleanRule> INFINITE_SPIN_TILE_RANGE = GameRuleRegistry.register(
            "spinTilesInfiniteRange",
            GameRules.Category.MISC,
            GameRuleFactory.createBooleanRule(false)
    );
    public static final GameRules.Key<GameRules.BooleanRule> UNLIMITED_SPIN_TILE_COMBO = GameRuleRegistry.register(
            "spinTilesUnlimitedCombo",
            GameRules.Category.MISC,
            GameRuleFactory.createBooleanRule(false)
    );

    public static final SoundEvent SPIN_SOUND = SoundEvent.of(id("spin"));

    public static final SpinTileBlock SPIN_TILE = new SpinTileBlock(tileSettings());
    public static final SpinStopTileBlock SPIN_STOP_TILE = new SpinStopTileBlock(tileSettings());
    public static final IceTileBlock ICE_TILE = new IceTileBlock(iceSettings());

    private static final Item SPIN_TILE_ITEM = new BlockItem(SPIN_TILE, new Item.Settings());
    private static final Item SPIN_STOP_TILE_ITEM = new BlockItem(SPIN_STOP_TILE, new Item.Settings());
    private static final Item ICE_TILE_ITEM = new BlockItem(ICE_TILE, new Item.Settings());

    private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.cobblemon_spin_tiles"))
            .icon(() -> new ItemStack(SPIN_TILE_ITEM))
            .entries((context, entries) -> {
                entries.add(SPIN_TILE_ITEM);
                entries.add(SPIN_STOP_TILE_ITEM);
                entries.add(ICE_TILE_ITEM);
            })
            .build();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(SpinRidePayload.ID, SpinRidePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(IceSlidePayload.ID, IceSlidePayload.CODEC);
        Registry.register(Registries.SOUND_EVENT, id("spin"), SPIN_SOUND);
        Registry.register(Registries.BLOCK, id("spin_tile"), SPIN_TILE);
        Registry.register(Registries.BLOCK, id("spin_stop_tile"), SPIN_STOP_TILE);
        Registry.register(Registries.BLOCK, id("ice_tile"), ICE_TILE);
        Registry.register(Registries.ITEM, id("spin_tile"), SPIN_TILE_ITEM);
        Registry.register(Registries.ITEM, id("spin_stop_tile"), SPIN_STOP_TILE_ITEM);
        Registry.register(Registries.ITEM, id("ice_tile"), ICE_TILE_ITEM);
        Registry.register(Registries.ITEM_GROUP, id("main"), ITEM_GROUP);
        ServerTickEvents.END_SERVER_TICK.register(SpinRideController::tick);
        ServerTickEvents.END_SERVER_TICK.register(IceSlideController::tick);
    }

    static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    private static AbstractBlock.Settings tileSettings() {
        return AbstractBlock.Settings.create()
                .strength(0.2F)
                .requiresTool()
                .sounds(BlockSoundGroup.METAL)
                .nonOpaque()
                .dynamicBounds();
    }

    private static AbstractBlock.Settings iceSettings() {
        return AbstractBlock.Settings.create()
                .strength(0.5F)
                .requiresTool()
                .sounds(BlockSoundGroup.GLASS)
                .slipperiness(0.98F);
    }
}
