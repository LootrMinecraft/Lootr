package noobanidus.mods.lootr.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
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
      return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(LootrAPI.MODID, name));
    }
  }

  public static class Items {
    public static final TagKey<Item> CHESTS = tag("chests");
    public static final TagKey<Item> TRAPPED_CHESTS = tag("trapped_chests");
    public static final TagKey<Item> SHULKERS = tag("shulkers");
    public static final TagKey<Item> BARRELS = tag("barrels");
    public static final TagKey<Item> CONTAINERS = tag("containers");

    static TagKey<Item> tag(String name) {
      return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(LootrAPI.MODID, name));
    }
  }

  public static class Structures {
    public static final TagKey<ConfiguredStructureFeature<?, ?>> DESERT_PYRAMID = tag("desert_pyramid");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> JUNGLE_TEMPLE = tag("jungle_temple");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> STRUCTURE_BLACKLIST = tag("structure_blacklist");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> REFRESH_STRUCTURES = tag("refresh_structures");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> DECAY_STRUCTURES = tag("decay_structures");

    static TagKey<ConfiguredStructureFeature<?, ?>> tag(String name) {
      return TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(LootrAPI.MODID, name));
    }
  }
}
