package noobanidus.mods.lootr.common.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

public final class LootrConstants {
  private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
    BlockEntity blockentity = level.getBlockEntity(pos);
    if (LootrAPI.wrapBlockEntity(blockentity) instanceof ILootrBlockEntity ibe) {
      return !ibe.isPhysicallyOpen();
    }
    return false;
  };

  public static final Identifier SHERDSAPI_POT_DECORATIONS = Identifier.fromNamespaceAndPath("sherdsapi", "stack_pot_decorations");
  public static final Identifier SHERDSAPI_SHERD_PATTERN = Identifier.fromNamespaceAndPath("sherdsapi", "sherd_pattern");

  public static class Identifiers {
    public static final Identifier CHEST = LootrAPI.rl("chest");
    public static final Identifier TRAPPED_CHEST = LootrAPI.rl("trapped_chest");
    public static final Identifier SHULKER_BOX = LootrAPI.rl("shulker_box");
    public static final Identifier BARREL = LootrAPI.rl("barrel");
    public static final Identifier INVENTORY = LootrAPI.rl("inventory");
    public static final Identifier MINECART_WITH_CHEST = LootrAPI.rl("chest_minecart");
    public static final Identifier TROPHY = LootrAPI.rl("trophy");
    public static final Identifier DECORATED_POT = LootrAPI.rl("decorated_pot");
    public static final Identifier BRUSHABLE_BLOCK = LootrAPI.rl("brushable_block");
    public static final Identifier SUSPICIOUS_SAND = LootrAPI.rl("suspicious_sand");
    public static final Identifier SUSPICIOUS_GRAVEL = LootrAPI.rl("suspicious_gravel");
    public static final Identifier ITEM_FRAME = LootrAPI.rl("item_frame");
    public static final Identifier UNOPENED_PARTICLE = LootrAPI.rl("unopened_particle");
    public static final Identifier REFRESH_PARTICLE = LootrAPI.rl("refresh_particle");
    public static final Identifier SIMPLE = LootrAPI.rl("simple");
    // Tags for entities
    public static final Identifier CAN_CONVERT = LootrAPI.rl("lootr_can_convert_item_frame");
    public static final Identifier COPPER_CHEST = LootrAPI.rl("copper_chest");
    public static final Identifier EXPOSED_COPPER_CHEST = LootrAPI.rl("exposed_copper_chest");
    public static final Identifier WEATHERED_COPPER_CHEST = LootrAPI.rl("weathered_copper_chest");
    public static final Identifier OXIDIZED_COPPER_CHEST = LootrAPI.rl("oxidized_copper_chest");
  }

  public static class LootrBlockEntityIds {
    public static final ResourceKey<BlockEntityType<?>> CHEST = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, Identifiers.CHEST);
    public static final ResourceKey<BlockEntityType<?>> TRAPPED_CHEST = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, Identifiers.TRAPPED_CHEST);
    public static final ResourceKey<BlockEntityType<?>> BARREL = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, Identifiers.BARREL);
    public static final ResourceKey<BlockEntityType<?>> SHULKER_BOX = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, Identifiers.SHULKER_BOX);
    public static final ResourceKey<BlockEntityType<?>> BRUSHABLE_BLOCK = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, Identifiers.BRUSHABLE_BLOCK);
    public static final ResourceKey<BlockEntityType<?>> DECORATED_POT = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, Identifiers.DECORATED_POT);
  }

  public static class LootrEntityIds {
    public static final ResourceKey<EntityType<?>> ITEM_FRAME_ENTITY = ResourceKey.create(Registries.ENTITY_TYPE, Identifiers.ITEM_FRAME);
    public static final ResourceKey<EntityType<?>> MINECART_WITH_CHEST_ENTITY = ResourceKey.create(Registries.ENTITY_TYPE, Identifiers.MINECART_WITH_CHEST);
  }

  public static final String CAN_CONVERT_TAG = Identifiers.CAN_CONVERT.toString();

  public static final String LOOTR_DATA_DIRECTORY = "lootr";
  public static final String REGION_DIRECTORY = "region";
  public static final String MCA_FILE_EXTENSION = ".mca";

  public static class LootrBlockIds {
    public static final ResourceKey<Block> CHEST = ResourceKey.create(Registries.BLOCK, Identifiers.CHEST);
    public static final ResourceKey<Block> TRAPPED_CHEST = ResourceKey.create(Registries.BLOCK, Identifiers.TRAPPED_CHEST);
    public static final ResourceKey<Block> BARREL = ResourceKey.create(Registries.BLOCK, Identifiers.BARREL);
    public static final ResourceKey<Block> SHULKER_BOX = ResourceKey.create(Registries.BLOCK, Identifiers.SHULKER_BOX);
    public static final ResourceKey<Block> SUSPICIOUS_SAND = ResourceKey.create(Registries.BLOCK, Identifiers.SUSPICIOUS_SAND);
    public static final ResourceKey<Block> SUSPICIOUS_GRAVEL = ResourceKey.create(Registries.BLOCK, Identifiers.SUSPICIOUS_GRAVEL);
    public static final ResourceKey<Block> DECORATED_POT = ResourceKey.create(Registries.BLOCK, Identifiers.DECORATED_POT);
    public static final ResourceKey<Block> TROPHY = ResourceKey.create(Registries.BLOCK, Identifiers.TROPHY);
    public static final ResourceKey<Block> COPPER_CHEST = ResourceKey.create(Registries.BLOCK, Identifiers.COPPER_CHEST);
    public static final ResourceKey<Block> EXPOSED_COPPER_CHEST = ResourceKey.create(Registries.BLOCK, Identifiers.EXPOSED_COPPER_CHEST);
    public static final ResourceKey<Block> OXIDIZED_COPPER_CHEST = ResourceKey.create(Registries.BLOCK, Identifiers.OXIDIZED_COPPER_CHEST);
    public static final ResourceKey<Block> WEATHERED_COPPER_CHEST = ResourceKey.create(Registries.BLOCK, Identifiers.WEATHERED_COPPER_CHEST);
  }

  public static class LootrItemIds {
    public static final ResourceKey<Item> CHEST = ResourceKey.create(Registries.ITEM, Identifiers.CHEST);
    public static final ResourceKey<Item> TRAPPED_CHEST = ResourceKey.create(Registries.ITEM, Identifiers.TRAPPED_CHEST);
    public static final ResourceKey<Item> BARREL = ResourceKey.create(Registries.ITEM, Identifiers.BARREL);
    public static final ResourceKey<Item> SHULKER_BOX = ResourceKey.create(Registries.ITEM, Identifiers.SHULKER_BOX);
    public static final ResourceKey<Item> SUSPICIOUS_SAND = ResourceKey.create(Registries.ITEM, Identifiers.SUSPICIOUS_SAND);
    public static final ResourceKey<Item> SUSPICIOUS_GRAVEL = ResourceKey.create(Registries.ITEM, Identifiers.SUSPICIOUS_GRAVEL);
    public static final ResourceKey<Item> DECORATED_POT = ResourceKey.create(Registries.ITEM, Identifiers.DECORATED_POT);
    public static final ResourceKey<Item> TROPHY = ResourceKey.create(Registries.ITEM, Identifiers.TROPHY);
    public static final ResourceKey<Item> COPPER_CHEST = ResourceKey.create(Registries.ITEM, Identifiers.COPPER_CHEST);
    public static final ResourceKey<Item> WEATHERED_COPPER_CHEST = ResourceKey.create(Registries.ITEM, Identifiers.WEATHERED_COPPER_CHEST);
    public static final ResourceKey<Item> OXIDIZED_COPPER_CHEST = ResourceKey.create(Registries.ITEM, Identifiers.OXIDIZED_COPPER_CHEST);
    public static final ResourceKey<Item> EXPOSED_COPPER_CHEST = ResourceKey.create(Registries.ITEM, Identifiers.EXPOSED_COPPER_CHEST);
  }

  public static class BlockProperties {
    public static final BlockBehaviour.Properties CHEST = BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)
        .strength(2.5f).setId(LootrBlockIds.CHEST);
    public static final BlockBehaviour.Properties TRAPPED_CHEST = BlockBehaviour.Properties.ofFullCopy(Blocks.TRAPPED_CHEST)
        .strength(2.5f).setId(LootrBlockIds.TRAPPED_CHEST);
    public static final BlockBehaviour.Properties BARREL = BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
        .strength(2.5f).setId(LootrBlockIds.BARREL);
    public static final BlockBehaviour.Properties TROPHY = BlockBehaviour.Properties.of().strength(15f)
        .sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15).setId(LootrBlockIds.TROPHY);
    public static final BlockBehaviour.Properties SHULKER_BOX = BlockBehaviour.Properties.of().strength(2.5f)
        .dynamicShape().noOcclusion().forceSolidOn().pushReaction(PushReaction.DESTROY).isSuffocating(posPredicate)
        .isViewBlocking(posPredicate).setId(LootrBlockIds.SHULKER_BOX);
    public static final BlockBehaviour.Properties SUSPICIOUS_SAND = BlockBehaviour.Properties.ofFullCopy(Blocks.SUSPICIOUS_SAND)
        .strength(2.5f).setId(LootrBlockIds.SUSPICIOUS_SAND);
    public static final BlockBehaviour.Properties SUSPICIOUS_GRAVEL = BlockBehaviour.Properties.ofFullCopy(Blocks.SUSPICIOUS_GRAVEL)
        .strength(2.5f).setId(LootrBlockIds.SUSPICIOUS_GRAVEL);
    public static final BlockBehaviour.Properties DECORATED_POT = BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT)
        .strength(1.5f).sound(SoundType.DECORATED_POT).setId(LootrBlockIds.DECORATED_POT);
    public static final BlockBehaviour.Properties COPPER_CHEST = BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_CHEST.waxed()
        .unaffected()).strength(2.5f).setId(LootrBlockIds.COPPER_CHEST);
    public static final BlockBehaviour.Properties WEATHERED_COPPER_CHEST = BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_CHEST.waxed()
        .weathered()).strength(2.5f).setId(LootrBlockIds.WEATHERED_COPPER_CHEST);
    public static final BlockBehaviour.Properties EXPOSED_COPPER_CHEST = BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_CHEST.waxed()
        .exposed()).strength(2.5f).setId(LootrBlockIds.EXPOSED_COPPER_CHEST);
    public static final BlockBehaviour.Properties OXIDIZED_COPPER_CHEST = BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_CHEST.waxed()
        .oxidized()).strength(2.5f).setId(LootrBlockIds.OXIDIZED_COPPER_CHEST);
  }

}
