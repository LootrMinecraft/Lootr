package noobanidus.mods.lootr.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import noobanidus.mods.lootr.client.item.SpecialLootChestItemRenderer;

public class ItemLootrChestBlock extends ItemBlock {
    public ItemLootrChestBlock(Block block) {
        super(block);
        setTileEntityItemStackRenderer(new SpecialLootChestItemRenderer());
    }
}
