package noobanidus.mods.lootr;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.api.LootrAPI;

public class LootrTags {
  public static class Blocks extends LootrTags {
    public static TagKey<Block> CHESTS = tag("chests");
    public static TagKey<Block> TRAPPED_CHESTS = tag("trapped_chests");
    public static TagKey<Block> SHULKERS = tag("shulkers");
    public static TagKey<Block> BARRELS = tag("barrels");
    public static TagKey<Block> CONTAINERS = tag("containers");

    static TagKey<Block> tag(String name) {
      return BlockTags.create(new ResourceLocation(LootrAPI.MODID, name));
    }
  }

  public static class Items extends LootrTags {
    public static TagKey<Item> CHESTS = tag("chests");
    public static TagKey<Item> TRAPPED_CHESTS = tag("trapped_chests");
    public static TagKey<Item> SHULKERS = tag("shulkers");
    public static TagKey<Item> BARRELS = tag("barrels");
    public static TagKey<Item> CONTAINERS = tag("containers");

    static TagKey<Item> tag(String name) {
      return ItemTags.create(new ResourceLocation(LootrAPI.MODID, name));
    }
  }
}
