package noobanidus.mods.lootr;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.api.LootrAPI;

public class LootrTags {
  public static class Blocks extends LootrTags {
    public static final TagKey<Block> CONVERT_CHESTS = tag("convert/chests");
    public static final TagKey<Block> CONVERT_TRAPPED_CHESTS = tag("convert/trapped_chests");
    public static final TagKey<Block> CONVERT_SHULKERS = tag("convert/shulkers");
    public static final TagKey<Block> CONVERT_BARRELS = tag("convert/barrels");
    public static final TagKey<Block> CONVERT_BLOCK = tag("convert/blocks");

    public static final TagKey<Block> CHESTS = tag("chests");
    public static final TagKey<Block> TRAPPED_CHESTS = tag("trapped_chests");
    public static final TagKey<Block> SHULKERS = tag("shulkers");
    public static final TagKey<Block> BARRELS = tag("barrels");
    public static final TagKey<Block> CONTAINERS = tag("containers");

    static TagKey<Block> tag(String name) {
      return TagKey.create(Registries.BLOCK, LootrAPI.rl(name));
    }
  }

  public static class Items extends LootrTags {
    public static TagKey<Item> CHESTS = tag("chests");
    public static TagKey<Item> TRAPPED_CHESTS = tag("trapped_chests");
    public static TagKey<Item> SHULKERS = tag("shulkers");
    public static TagKey<Item> BARRELS = tag("barrels");
    public static TagKey<Item> CONTAINERS = tag("containers");

    static TagKey<Item> tag(String name) {
      return TagKey.create(Registries.ITEM, LootrAPI.rl(name));
    }
  }
}