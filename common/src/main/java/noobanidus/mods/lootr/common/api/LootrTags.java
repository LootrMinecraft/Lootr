package noobanidus.mods.lootr.common.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class LootrTags {
  public static class Blocks {
    public static final TagKey<Block> CONVERT_CHESTS = tag("convert/chests");
    public static final TagKey<Block> CONVERT_COPPER_CHESTS = tag("convert/copper_chests");
    public static final TagKey<Block> CONVERT_WEATHERED_COPPER_CHESTS = tag("convert/weathered_copper_chests");
    public static final TagKey<Block> CONVERT_EXPOSED_COPPER_CHESTS = tag("convert/exposed_copper_chests");
    public static final TagKey<Block> CONVERT_OXIDIZED_COPPER_CHESTS = tag("convert/oxidized_copper_chests");
    public static final TagKey<Block> CONVERT_TRAPPED_CHESTS = tag("convert/trapped_chests");
    public static final TagKey<Block> CONVERT_SHULKERS = tag("convert/shulkers");
    public static final TagKey<Block> CONVERT_BARRELS = tag("convert/barrels");
    public static final TagKey<Block> CONVERT_GRAVELS = tag("convert/gravels");
    public static final TagKey<Block> CONVERT_SANDS = tag("convert/sands");
    public static final TagKey<Block> CONVERT_POTS = tag("convert/pots");
    public static final TagKey<Block> CONVERT_BLOCK = tag("convert/blocks");
    public static final TagKey<Block> CONVERT_BLACKLIST = tag("convert/blacklist");

    public static final TagKey<Block> PREVENT_BREAK = tag("prevent_break");
    public static final TagKey<Block> PREVENT_BREAK_CHESTS = tag("prevent_break/chests");
    public static final TagKey<Block> PREVENT_BREAK_COPPER_CHESTS = tag("prevent_break/copper_chests");
    public static final TagKey<Block> PREVENT_BREAK_WEATHERED_COPPER_CHESTS = tag("prevent_break/weathered_copper_chests");
    public static final TagKey<Block> PREVENT_BREAK_EXPOSED_COPPER_CHESTS = tag("prevent_break/exposed_copper_chests");
    public static final TagKey<Block> PREVENT_BREAK_OXIDIZED_COPPER_CHESTS = tag("prevent_break/oxidized_copper_chests");
    public static final TagKey<Block> PREVENT_BREAK_TRAPPED_CHESTS = tag("prevent_break/trapped_chests");
    public static final TagKey<Block> PREVENT_BREAK_SHULKERS = tag("prevent_break/shulkers");
    public static final TagKey<Block> PREVENT_BREAK_BARRELS = tag("prevent_break/barrels");
    public static final TagKey<Block> PREVENT_BREAK_GRAVELS = tag("prevent_break/gravels");
    public static final TagKey<Block> PREVENT_BREAK_SANDS = tag("prevent_break/sands");
    public static final TagKey<Block> PREVENT_BREAK_POTS = tag("prevent_break/pots");
    public static final TagKey<Block> ENABLE_BREAK = tag("enable_break");

    public static final TagKey<Block> CHESTS = tag("chests");
    public static final TagKey<Block> TRAPPED_CHESTS = tag("trapped_chests");
    public static final TagKey<Block> COPPER_CHESTS = tag("copper_chests");
    public static final TagKey<Block> WEATHERED_COPPER_CHESTS = tag("weathered_copper_chests");
    public static final TagKey<Block> OXIDIZED_COPPER_CHESTS = tag("oxidized_copper_chests");
    public static final TagKey<Block> EXPOSED_COPPER_CHESTS = tag("exposed_copper_chests");

    public static final TagKey<Block> SHULKERS = tag("shulkers");
    public static final TagKey<Block> BARRELS = tag("barrels");
    public static final TagKey<Block> GRAVELS = tag("gravels");
    public static final TagKey<Block> SANDS = tag("sands");
    public static final TagKey<Block> POTS = tag("pots");
    public static final TagKey<Block> CONTAINERS = tag("containers");

    public static final TagKey<Block> CUSTOM_ELIGIBLE = tag("convert/custom_eligible");

    public static final TagKey<Block> CATS_CAN_BLOCK = tag("cats_can_block");

    public static final TagKey<Block> NON_BLOCKING = tag("non_blocking");

    public static final TagKey<Block> INTERACT_WHITELIST_BLOCKS = tag("interact_whitelist_blocks");
    public static final TagKey<Block> INTERACT_WHITELIST = tag("ftbchunks", "interact_whitelist");

    static TagKey<Block> tag(String name) {
      return TagKey.create(Registries.BLOCK, LootrAPI.rl(name));
    }

    static TagKey<Block> tag(String namespace, String name) {
      return TagKey.create(Registries.BLOCK, LootrAPI.rl(namespace, name));
    }
  }

  public static class Items {
    public static TagKey<Item> CHESTS = tag("chests");
    public static TagKey<Item> COPPER_CHESTS = tag("copper_chests");
    public static TagKey<Item> WEATHERED_COPPER_CHESTS = tag("weathered_copper_chests");
    public static TagKey<Item> OXIDIZED_COPPER_CHESTS = tag("oxidized_copper_chests");
    public static TagKey<Item> EXPOSED_COPPER_CHESTS = tag("exposed_copper_chests");
    public static TagKey<Item> TRAPPED_CHESTS = tag("trapped_chests");
    public static TagKey<Item> SHULKERS = tag("shulkers");
    public static TagKey<Item> BARRELS = tag("barrels");
    public static TagKey<Item> GRAVELS = tag("gravels");
    public static TagKey<Item> SANDS = tag("sands");
    public static TagKey<Item> POTS = tag("pots");
    public static TagKey<Item> CONTAINERS = tag("containers");

    public static TagKey<Item> ITEM_FRAME_CONVERT_BLACKLIST = tag("convert/item_frame_blacklist");

    static TagKey<Item> tag(String name) {
      return TagKey.create(Registries.ITEM, LootrAPI.rl(name));
    }
  }

  public static class Entity {
    public static TagKey<EntityType<?>> CONVERT_CARTS = tag("convert/minecarts");
    public static TagKey<EntityType<?>> CONVERT_ITEM_FRAMES = tag("convert/item_frames");
    public static TagKey<EntityType<?>> CONVERT_ENTITIES = tag("convert/entities");
    public static TagKey<EntityType<?>> CONVERT_BLACKLIST = tag("blacklist");

    public static final TagKey<EntityType<?>> PREVENT_BREAK = tag("prevent_break");
    public static final TagKey<EntityType<?>> PREVENT_BREAK_MINECARTS = tag("prevent_break/minecarts");
    public static final TagKey<EntityType<?>> PREVENT_BREAK_ITEM_FRAMES = tag("prevent_break/item_frames");
    public static final TagKey<EntityType<?>> ENABLE_BREAK = tag("enable_break");

    public static TagKey<EntityType<?>> MINECARTS = tag("minecarts");
    public static TagKey<EntityType<?>> ITEM_FRAMES = tag("item_frames");

    public static TagKey<EntityType<?>> CONTAINERS = tag("containers");

    static TagKey<EntityType<?>> tag(String name) {
      return TagKey.create(Registries.ENTITY_TYPE, LootrAPI.rl(name));
    }
  }

  public static class BlockEntity {
    public static TagKey<BlockEntityType<?>> LOOTR_OBJECT = tag("object");
    public static TagKey<BlockEntityType<?>> TRAPPED = tag("trapped");
    public static TagKey<BlockEntityType<?>> CUSTOM_INELIGIBLE = tag("custom_ineligible");
    public static TagKey<BlockEntityType<?>> CONVERT_BLACKLIST = tag("convert/blacklist");

    @Deprecated
    public static boolean isTagged(net.minecraft.world.level.block.entity.BlockEntity blockEntity, TagKey<BlockEntityType<?>> tag) {
      return blockEntity.is(tag);
    }

    static TagKey<BlockEntityType<?>> tag(String name) {
      return TagKey.create(Registries.BLOCK_ENTITY_TYPE, LootrAPI.rl(name));
    }
  }

  public static class Structure {
    public static TagKey<net.minecraft.world.level.levelgen.structure.Structure> STRUCTURE_BLACKLIST = tag("blacklist");
    public static TagKey<net.minecraft.world.level.levelgen.structure.Structure> STRUCTURE_WHITELIST = tag("whitelist");

    public static TagKey<net.minecraft.world.level.levelgen.structure.Structure> REFRESH_STRUCTURES = tag("refresh");
    public static TagKey<net.minecraft.world.level.levelgen.structure.Structure> DECAY_STRUCTURES = tag("decay");

    static TagKey<net.minecraft.world.level.levelgen.structure.Structure> tag(String name) {
      return TagKey.create(Registries.STRUCTURE, LootrAPI.rl(name));
    }
  }
}
