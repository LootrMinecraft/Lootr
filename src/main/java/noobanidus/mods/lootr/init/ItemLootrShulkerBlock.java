package noobanidus.mods.lootr.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import noobanidus.mods.lootr.client.item.SpecialLootShulkerItemRenderer;

public class ItemLootrShulkerBlock extends ItemBlock {

    public ItemLootrShulkerBlock(Block block) {
        super(block);
        setTileEntityItemStackRenderer(new SpecialLootShulkerItemRenderer());
    }
}
