package noobanidus.mods.lootr.common.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
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

  // TODO: Rename these eventually.
  public static final Identifier LOOTR_CHEST = LootrAPI.rl("lootr_chest");
  public static final Identifier LOOTR_TRAPPED_CHEST = LootrAPI.rl("lootr_trapped_chest");
  public static final Identifier LOOTR_SHULKER = LootrAPI.rl("lootr_shulker");
  public static final Identifier LOOTR_BARREL = LootrAPI.rl("lootr_barrel");
  public static final Identifier LOOTR_INVENTORY = LootrAPI.rl("lootr_inventory");
  public static final Identifier LOOTR_CART = LootrAPI.rl("lootr_cart");

  public static final ResourceKey<EntityType<?>> FABRIC_LOOTR_CART_ENTITY = ResourceKey.create(Registries.ENTITY_TYPE, LOOTR_CART);

  // Into these
  public static final Identifier CHEST = LootrAPI.rl("chest");
  public static final Identifier TRAPPED_CHEST = LootrAPI.rl("trapped_chest");

  // TODO: Migrate shulker -> shulker_box
  public static final Identifier SHULKER = LootrAPI.rl("shulker");

  public static final Identifier SHULKER_BOX = LootrAPI.rl("shulker_box");
  public static final Identifier BARREL = LootrAPI.rl("barrel");
  public static final Identifier INVENTORY = LootrAPI.rl("inventory");
  public static final Identifier MINECART = LootrAPI.rl("chest_minecart");
  public static final Identifier TROPHY = LootrAPI.rl("trophy");

  // These don't need to be migrated
  public static final Identifier DECORATED_POT = LootrAPI.rl("decorated_pot");
  public static final Identifier BRUSHABLE_BLOCK = LootrAPI.rl("brushable_block");
  public static final Identifier SUSPICIOUS_SAND = LootrAPI.rl("suspicious_sand");
  public static final Identifier SUSPICIOUS_GRAVEL = LootrAPI.rl("suspicious_gravel");
  public static final Identifier ITEM_FRAME = LootrAPI.rl("item_frame");
  public static final Identifier UNOPENED_PARTICLE = LootrAPI.rl("unopened_particle");

  public static final ResourceKey<EntityType<?>> ITEM_FRAME_ENTITY = ResourceKey.create(Registries.ENTITY_TYPE, ITEM_FRAME);
  public static final ResourceKey<EntityType<?>> NEOFORGE_MINECART_WITH_CHEST = ResourceKey.create(Registries.ENTITY_TYPE, LootrAPI.rl("lootr_minecart"));

  public static final Identifier SIMPLE = LootrAPI.rl("simple");

  // Tags for entities
  public static final Identifier CAN_CONVERT = LootrAPI.rl("lootr_can_convert_item_frame");
  public static final String CAN_CONVERT_TAG = CAN_CONVERT.toString();

  // TODO: These are for backwards-compatibility
  public static final String LOOTR_SPECIAL_CHEST = "lootr:special_loot_chest";
  public static final String LOOTR_SPECIAL_BARREL = "lootr:special_loot_barrel";
  public static final String LOOTR_SPECIAL_TRAPPED_CHEST = "lootr:special_trapped_loot_chest";
  public static final String LOOTR_SPECIAL_SHULKER = "lootr:special_loot_shulker";
  public static final String LOOTR_SPECIAL_INVENTORY = "lootr:special_loot_inventory";

  public static final String LOOTR_DATA_DIRECTORY = "lootr";
  public static final String REGION_DIRECTORY = "region";
  public static final String MCA_FILE_EXTENSION = ".mca";

  public static final BlockBehaviour.Properties CHEST_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)
      .strength(2.5f);
  public static final BlockBehaviour.Properties TRAPPED_CHEST_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.TRAPPED_CHEST)
      .strength(2.5f);
  public static final BlockBehaviour.Properties BARREL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
      .strength(2.5f);
  public static final BlockBehaviour.Properties INVENTORY_PROPERTIES = BlockBehaviour.Properties.of().strength(2.5f)
      .sound(SoundType.WOOD);

  public static final BlockBehaviour.Properties TROPHY_PROPERTIES = BlockBehaviour.Properties.of().strength(15f)
      .sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15);

  public static final BlockBehaviour.Properties SHULKER_BOX_PROPERTIES = BlockBehaviour.Properties.of().strength(2.5f)
      .dynamicShape().noOcclusion().forceSolidOn().pushReaction(PushReaction.DESTROY).isSuffocating(posPredicate)
      .isViewBlocking(posPredicate);

  public static final BlockBehaviour.Properties SUSPICIOUS_SAND_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.SUSPICIOUS_SAND)
      .strength(2.5f);
  public static final BlockBehaviour.Properties SUSPICIOUS_GRAVEL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.SUSPICIOUS_GRAVEL)
      .strength(2.5f);

  public static final BlockBehaviour.Properties DECORATED_POT_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT)
      .strength(1.5f).sound(SoundType.DECORATED_POT);

}
