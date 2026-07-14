package dev.siah.spintiles;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public final class IceTileBlock extends Block {
    public static final MapCodec<IceTileBlock> CODEC = createCodec(IceTileBlock::new);

    public IceTileBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }
}
