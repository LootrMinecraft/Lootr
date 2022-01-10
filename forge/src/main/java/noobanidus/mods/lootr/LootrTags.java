package noobanidus.mods.lootr;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

public class LootrTags {
  public static class Blocks extends LootrTags {
    public static Tags.IOptionalNamedTag<Block> CHESTS = tag("chests");
    public static Tags.IOptionalNamedTag<Block> TRAPPED_CHESTS = tag("trapped_chests");
    public static Tags.IOptionalNamedTag<Block> SHULKERS = tag("shulkers");
    public static Tags.IOptionalNamedTag<Block> BARRELS = tag("barrels");
    public static Tags.IOptionalNamedTag<Block> CONTAINERS = tag("containers");

    static Tags.IOptionalNamedTag<Block> tag(String name) {
      return BlockTags.createOptional(new ResourceLocation(Lootr.MODID, name));
    }
  }

  public static class Items extends LootrTags {
    public static Tags.IOptionalNamedTag<Item> CHESTS = tag("chests");
    public static Tags.IOptionalNamedTag<Item> TRAPPED_CHESTS = tag("trapped_chests");
    public static Tags.IOptionalNamedTag<Item> SHULKERS = tag("shulkers");
    public static Tags.IOptionalNamedTag<Item> BARRELS = tag("barrels");
    public static Tags.IOptionalNamedTag<Item> CONTAINERS = tag("containers");

    static Tags.IOptionalNamedTag<Item> tag(String name) {
      return ItemTags.createOptional(new ResourceLocation(Lootr.MODID, name));
    }
  }
}
