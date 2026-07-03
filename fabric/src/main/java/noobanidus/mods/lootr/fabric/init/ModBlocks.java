package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.block.*;

public class ModBlocks {
  public static final LootrChestBlock CHEST = new LootrChestBlock(LootrConstants.CHEST_PROPERTIES);
  public static final LootrCopperChestBlock COPPER_CHEST = new LootrCopperChestBlock(LootrCopperChestBlock.COPPER, LootrConstants.COPPER_CHEST_PROPERTIES);
  public static final LootrCopperChestBlock EXPOSED_COPPER_CHEST = new LootrCopperChestBlock(LootrCopperChestBlock.EXPOSED, LootrConstants.EXPOSED_COPPER_CHEST_PROPERTIES);
  public static final LootrCopperChestBlock WEATHERED_COPPER_CHEST = new LootrCopperChestBlock(LootrCopperChestBlock.WEATHERED, LootrConstants.WEATHERED_COPPER_CHEST_PROPERTIES);
  public static final LootrCopperChestBlock OXIDIZED_COPPER_CHEST = new LootrCopperChestBlock(LootrCopperChestBlock.OXIDIZED, LootrConstants.OXIDIZED_COPPER_CHEST_PROPERTIES);
  public static final LootrBarrelBlock BARREL = new LootrBarrelBlock(LootrConstants.BARREL_PROPERTIES);
  public static final LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(LootrConstants.TRAPPED_CHEST_PROPERTIES);
  public static final Block TROPHY = new TrophyBlock(LootrConstants.TROPHY_PROPERTIES);
  public static final LootrShulkerBoxBlock SHULKER_BOX = new LootrShulkerBoxBlock(LootrConstants.SHULKER_BOX_PROPERTIES);

  public static final LootrBrushableBlock SUSPICIOUS_SAND = new LootrBrushableBlock(Blocks.SAND, ((BrushableBlock) Blocks.SUSPICIOUS_SAND).getBrushSound(), ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.SUSPICIOUS_SAND_PROPERTIES);
  public static final LootrBrushableBlock SUSPICIOUS_GRAVEL = new LootrBrushableBlock(Blocks.GRAVEL, ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushSound(), ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.SUSPICIOUS_GRAVEL_PROPERTIES);

  public static final LootrDecoratedPotBlock DECORATED_POT = new LootrDecoratedPotBlock(LootrConstants.DECORATED_POT_PROPERTIES);

  public static void registerBlocks() {
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.CHEST, CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.BARREL, BARREL);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.SHULKER_BOX, SHULKER_BOX);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.SUSPICIOUS_SAND, SUSPICIOUS_SAND);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.SUSPICIOUS_GRAVEL, SUSPICIOUS_GRAVEL);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.DECORATED_POT, DECORATED_POT);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.TROPHY, TROPHY);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.COPPER_CHEST, COPPER_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.EXPOSED_COPPER_CHEST, EXPOSED_COPPER_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.OXIDIZED_COPPER_CHEST, OXIDIZED_COPPER_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.WEATHERED_COPPER_CHEST, WEATHERED_COPPER_CHEST);
  }
}
