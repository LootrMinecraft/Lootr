package noobanidus.mods.lootr;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import noobanidus.mods.lootr.api.LootrAPI;

public class LootrTags {
  public static class Blocks {
    public static final TagKey<Block> CHESTS = tag("chests");
    public static final TagKey<Block> TRAPPED_CHESTS = tag("trapped_chests");
    public static final TagKey<Block> SHULKERS = tag("shulkers");
    public static final TagKey<Block> BARRELS = tag("barrels");
    public static final TagKey<Block> CONTAINERS = tag("containers");

    public static final TagKey<Block> CONVERT_CHESTS = tag("convert/chests");
    public static final TagKey<Block> CONVERT_TRAPPED_CHESTS = tag("convert/trapped_chests");
    public static final TagKey<Block> CONVERT_SHULKERS = tag("convert/shulkers");
    public static final TagKey<Block> CONVERT_BARRELS = tag("convert/barrels");

    static TagKey<Block> tag(String name) {
      return TagKey.create(Registries.BLOCK, new ResourceLocation(LootrAPI.MODID, name));
    }
  }

  public static class Items {
    public static final TagKey<Item> CHESTS = tag("chests");
    public static final TagKey<Item> TRAPPED_CHESTS = tag("trapped_chests");
    public static final TagKey<Item> SHULKERS = tag("shulkers");
    public static final TagKey<Item> BARRELS = tag("barrels");
    public static final TagKey<Item> CONTAINERS = tag("containers");

    static TagKey<Item> tag(String name) {
      return TagKey.create(Registries.ITEM, new ResourceLocation(LootrAPI.MODID, name));
    }
  }

  public static class Structures {
    public static final TagKey<Structure> DESERT_PYRAMID = tag("desert_pyramid");
    public static final TagKey<Structure> JUNGLE_TEMPLE = tag("jungle_temple");
    public static final TagKey<Structure> STRUCTURE_BLACKLIST = tag("structure_blacklist");
    public static final TagKey<Structure> REFRESH_STRUCTURES = tag("refresh_structures");
    public static final TagKey<Structure> DECAY_STRUCTURES = tag("decay_structures");

    static TagKey<Structure> tag(String name) {
      return TagKey.create(Registries.STRUCTURE, new ResourceLocation(LootrAPI.MODID, name));
    }
  }
}
