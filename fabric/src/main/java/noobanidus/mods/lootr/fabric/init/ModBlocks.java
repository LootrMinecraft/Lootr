package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.block.*;

public class ModBlocks {
  public static final LootrChestBlock CHEST = new LootrChestBlock(LootrConstants.CHEST_PROPERTIES);
  public static final LootrBarrelBlock BARREL = new LootrBarrelBlock(LootrConstants.BARREL_PROPERTIES);
  public static final LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(LootrConstants.TRAPPED_CHEST_PROPERTIES);
  public static final LootrInventoryBlock INVENTORY = new LootrInventoryBlock(LootrConstants.INVENTORY_PROPERTIES);
  public static final Block TROPHY = new TrophyBlock(LootrConstants.TROPHY_PROPERTIES);
  public static final LootrShulkerBlock SHULKER_BOX = new LootrShulkerBlock(LootrConstants.SHULKER_BOX_PROPERTIES);

  public static final LootrBrushableBlock SUSPICIOUS_SAND = new LootrBrushableBlock(Blocks.SAND, ((BrushableBlock) Blocks.SUSPICIOUS_SAND).getBrushSound(), ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.SUSPICIOUS_SAND_PROPERTIES);
  public static final LootrBrushableBlock SUSPICIOUS_GRAVEL = new LootrBrushableBlock(Blocks.GRAVEL, ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushSound(), ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.SUSPICIOUS_GRAVEL_PROPERTIES);

  public static final LootrDecoratedPotBlock DECORATED_POT = new LootrDecoratedPotBlock(LootrConstants.DECORATED_POT_PROPERTIES);

  public static void registerBlocks() {
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.CHEST, CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.BARREL, BARREL);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.SHULKER_BOX, SHULKER_BOX);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.INVENTORY, INVENTORY);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.SUSPICIOUS_SAND, SUSPICIOUS_SAND);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.SUSPICIOUS_GRAVEL, SUSPICIOUS_GRAVEL);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.DECORATED_POT, DECORATED_POT);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.TROPHY, TROPHY);
  }
}
