package noobanidus.mods.lootr.common.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

public class LootrProperties {
  private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
    BlockEntity blockentity = level.getBlockEntity(pos);
    if (LootrAPI.resolveBlockEntity(blockentity) instanceof ILootrBlockEntity ibe) {
      return !ibe.isPhysicallyOpen();
    }
    return false;
  };

  public static final ResourceLocation SHERDSAPI_POT_DECORATIONS = ResourceLocation.fromNamespaceAndPath("sherdsapi", "stack_pot_decorations");
  public static final ResourceLocation SHERDSAPI_SHERD_PATTERN = ResourceLocation.fromNamespaceAndPath("sherdsapi", "sherd_pattern");

  // TODO: Rename these eventually.
  public static final ResourceLocation LOOTR_CHEST = LootrAPI.rl("lootr_chest");
  public static final ResourceLocation LOOTR_TRAPPED_CHEST = LootrAPI.rl("lootr_trapped_chest");
  public static final ResourceLocation LOOTR_SHULKER = LootrAPI.rl("lootr_shulker");
  public static final ResourceLocation LOOTR_BARREL = LootrAPI.rl("lootr_barrel");
  public static final ResourceLocation LOOTR_INVENTORY = LootrAPI.rl("lootr_inventory");
  public static final ResourceLocation LOOTR_CART = LootrAPI.rl("lootr_cart");

  // Into these
  public static final ResourceLocation CHEST = LootrAPI.rl("chest");
  public static final ResourceLocation TRAPPED_CHEST = LootrAPI.rl("trapped_chest");

  // TODO: Migrate shulker -> shulker_box
  public static final ResourceLocation SHULKER = LootrAPI.rl("shulker");

  public static final ResourceLocation SHULKER_BOX = LootrAPI.rl("shulker_box");
  public static final ResourceLocation BARREL = LootrAPI.rl("barrel");
  public static final ResourceLocation INVENTORY = LootrAPI.rl("inventory");
  public static final ResourceLocation MINECART = LootrAPI.rl("chest_minecart");
  public static final ResourceLocation TROPHY = LootrAPI.rl("trophy");

  // These don't need to be migrated
  public static final ResourceLocation DECORATED_POT = LootrAPI.rl("decorated_pot");
  public static final ResourceLocation BRUSHABLE_BLOCK = LootrAPI.rl("brushable_block");
  public static final ResourceLocation SUSPICIOUS_SAND = LootrAPI.rl("suspicious_sand");
  public static final ResourceLocation SUSPICIOUS_GRAVEL = LootrAPI.rl("suspicious_gravel");
  public static final ResourceLocation ITEM_FRAME = LootrAPI.rl("item_frame");

  public static final ResourceLocation SIMPLE = LootrAPI.rl("simple");

  // TODO: These are for backwards-compatibility
  public static final String LOOTR_SPECIAL_CHEST = "lootr:special_loot_chest";
  public static final String LOOTR_SPECIAL_BARREL = "lootr:special_loot_barrel";
  public static final String LOOTR_SPECIAL_TRAPPED_CHEST = "lootr:special_trapped_loot_chest";
  public static final String LOOTR_SPECIAL_SHULKER = "lootr:special_loot_shulker";
  public static final String LOOTR_SPECIAL_INVENTORY = "lootr:special_loot_inventory";

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
