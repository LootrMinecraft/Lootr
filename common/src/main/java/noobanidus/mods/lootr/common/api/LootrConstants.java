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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

public class LootrConstants {
  private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
    BlockEntity blockentity = level.getBlockEntity(pos);
    if (LootrAPI.resolveBlockEntity(blockentity) instanceof ILootrBlockEntity ibe) {
      return !ibe.isPhysicallyOpen();
    }
    return false;
  };

  public static final Identifier SHERDSAPI_POT_DECORATIONS = Identifier.fromNamespaceAndPath("sherdsapi", "stack_pot_decorations");
  public static final Identifier SHERDSAPI_SHERD_PATTERN = Identifier.fromNamespaceAndPath("sherdsapi", "sherd_pattern");

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

  public static final ResourceKey<EntityType<?>> ITEM_FRAME_ENTITY = ResourceKey.create(Registries.ENTITY_TYPE, ITEM_FRAME);
  public static final ResourceKey<EntityType<?>> MINECART_WITH_CHEST_ENTITY = ResourceKey.create(Registries.ENTITY_TYPE, MINECART_WITH_CHEST);

  public static final Identifier SIMPLE = LootrAPI.rl("simple");

  // Tags for entities
  public static final Identifier CAN_CONVERT = LootrAPI.rl("lootr_can_convert_item_frame");
  public static final String CAN_CONVERT_TAG = CAN_CONVERT.toString();

  public static final String LOOTR_DATA_DIRECTORY = "lootr";
  public static final String REGION_DIRECTORY = "region";
  public static final String MCA_FILE_EXTENSION = ".mca";

  public static final ResourceKey<Block> CHEST_RESOURCE_KEY = ResourceKey.create(Registries.BLOCK, CHEST);
  public static final ResourceKey<Block> TRAPPED_CHEST_RESOURCE_KEY = ResourceKey.create(Registries.BLOCK, TRAPPED_CHEST);
  public static final ResourceKey<Block> BARREL_RESOURCE_KEY = ResourceKey.create(Registries.BLOCK, BARREL);
  public static final ResourceKey<Block> INVENTORY_RESOURCE_KEY = ResourceKey.create(Registries.BLOCK, INVENTORY);
  public static final ResourceKey<Block> SHULKER_BOX_RESOURCE_KEY = ResourceKey.create(Registries.BLOCK, SHULKER_BOX);
  public static final ResourceKey<Block> SUSPICIOUS_SAND_RESOURCE_KEY = ResourceKey.create(Registries.BLOCK, SUSPICIOUS_SAND);
  public static final ResourceKey<Block> SUSPICIOUS_GRAVEL_RESOURCE_KEY =  ResourceKey.create(Registries.BLOCK, SUSPICIOUS_GRAVEL);
  public static final ResourceKey<Block> DECORATED_POT_RESOURCE_KEY = ResourceKey.create(Registries.BLOCK, DECORATED_POT);
  public static final ResourceKey<Block> TROPHY_RESOURCE_KEY = ResourceKey.create(Registries.BLOCK, TROPHY);

  public static final ResourceKey<Item> CHEST_ITEM_RESOURCE_KEY = ResourceKey.create(Registries.ITEM, CHEST);
  public static final ResourceKey<Item> TRAPPED_CHEST_ITEM_RESOURCE_KEY = ResourceKey.create(Registries.ITEM, TRAPPED_CHEST);
  public static final ResourceKey<Item> BARREL_ITEM_RESOURCE_KEY = ResourceKey.create(Registries.ITEM, BARREL);
  public static final ResourceKey<Item> INVENTORY_ITEM_RESOURCE_KEY = ResourceKey.create(Registries.ITEM, INVENTORY);
  public static final ResourceKey<Item> SHULKER_BOX_ITEM_RESOURCE_KEY = ResourceKey.create(Registries.ITEM, SHULKER_BOX);
  public static final ResourceKey<Item> SUSPICIOUS_SAND_ITEM_RESOURCE_KEY = ResourceKey.create(Registries.ITEM, SUSPICIOUS_SAND);
  public static final ResourceKey<Item> SUSPICIOUS_GRAVEL_ITEM_RESOURCE_KEY =  ResourceKey.create(Registries.ITEM, SUSPICIOUS_GRAVEL);
  public static final ResourceKey<Item> DECORATED_POT_ITEM_RESOURCE_KEY = ResourceKey.create(Registries.ITEM, DECORATED_POT);
  public static final ResourceKey<Item> TROPHY_ITEM_RESOURCE_KEY = ResourceKey.create(Registries.ITEM, TROPHY);

  public static final BlockBehaviour.Properties CHEST_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)
      .strength(2.5f).setId(CHEST_RESOURCE_KEY);
  public static final BlockBehaviour.Properties TRAPPED_CHEST_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.TRAPPED_CHEST)
      .strength(2.5f).setId(TRAPPED_CHEST_RESOURCE_KEY);
  public static final BlockBehaviour.Properties BARREL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
      .strength(2.5f).setId(BARREL_RESOURCE_KEY);
  public static final BlockBehaviour.Properties INVENTORY_PROPERTIES = BlockBehaviour.Properties.of().strength(2.5f)
      .sound(SoundType.WOOD).setId(INVENTORY_RESOURCE_KEY);

  public static final BlockBehaviour.Properties TROPHY_PROPERTIES = BlockBehaviour.Properties.of().strength(15f)
      .sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15).setId(TROPHY_RESOURCE_KEY);

  public static final BlockBehaviour.Properties SHULKER_BOX_PROPERTIES = BlockBehaviour.Properties.of().strength(2.5f)
      .dynamicShape().noOcclusion().forceSolidOn().pushReaction(PushReaction.DESTROY).isSuffocating(posPredicate)
      .isViewBlocking(posPredicate).setId(SHULKER_BOX_RESOURCE_KEY);

  public static final BlockBehaviour.Properties SUSPICIOUS_SAND_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.SUSPICIOUS_SAND)
      .strength(2.5f).setId(SUSPICIOUS_SAND_RESOURCE_KEY);
  public static final BlockBehaviour.Properties SUSPICIOUS_GRAVEL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.SUSPICIOUS_GRAVEL)
      .strength(2.5f).setId(SUSPICIOUS_GRAVEL_RESOURCE_KEY);

  public static final BlockBehaviour.Properties DECORATED_POT_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT)
      .strength(1.5f).sound(SoundType.DECORATED_POT).setId(DECORATED_POT_RESOURCE_KEY);

}
