package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.block.*;

public class ModBlocks {
  public static final LootrChestBlock CHEST = new LootrChestBlock(LootrConstants.BlockProperties.CHEST);
  public static final LootrBarrelBlock BARREL = new LootrBarrelBlock(LootrConstants.BlockProperties.BARREL);
  public static final LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(LootrConstants.BlockProperties.TRAPPED_CHEST);
  public static final LootrCopperChestBlock COPPER_CHEST = new LootrCopperChestBlock(LootrCopperChestBlock.COPPER, LootrConstants.BlockProperties.COPPER_CHEST);
  public static final LootrCopperChestBlock EXPOSED_COPPER_CHEST = new LootrCopperChestBlock(LootrCopperChestBlock.EXPOSED, LootrConstants.BlockProperties.EXPOSED_COPPER_CHEST);
  public static final LootrCopperChestBlock WEATHERED_COPPER_CHEST = new LootrCopperChestBlock(LootrCopperChestBlock.WEATHERED, LootrConstants.BlockProperties.WEATHERED_COPPER_CHEST);
  public static final LootrCopperChestBlock OXIDIZED_COPPER_CHEST = new LootrCopperChestBlock(LootrCopperChestBlock.OXIDIZED, LootrConstants.BlockProperties.OXIDIZED_COPPER_CHEST);
  public static final Block TROPHY = new TrophyBlock(LootrConstants.BlockProperties.TROPHY);
  public static final LootrShulkerBoxBlock SHULKER_BOX = new LootrShulkerBoxBlock(LootrConstants.BlockProperties.SHULKER_BOX);

  public static final LootrBrushableBlock SUSPICIOUS_SAND = new LootrBrushableBlock(Blocks.SAND, ((BrushableBlock) Blocks.SUSPICIOUS_SAND).getBrushSound(), ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.BlockProperties.SUSPICIOUS_SAND);
  public static final LootrBrushableBlock SUSPICIOUS_GRAVEL = new LootrBrushableBlock(Blocks.GRAVEL, ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushSound(), ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.BlockProperties.SUSPICIOUS_GRAVEL);

  public static final LootrDecoratedPotBlock DECORATED_POT = new LootrDecoratedPotBlock(LootrConstants.BlockProperties.DECORATED_POT);

  public static void registerBlocks() {
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.CHEST, CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.BARREL, BARREL);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.SHULKER_BOX, SHULKER_BOX);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.SUSPICIOUS_SAND, SUSPICIOUS_SAND);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.SUSPICIOUS_GRAVEL, SUSPICIOUS_GRAVEL);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.DECORATED_POT, DECORATED_POT);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.TROPHY, TROPHY);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.COPPER_CHEST, COPPER_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.EXPOSED_COPPER_CHEST, EXPOSED_COPPER_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.WEATHERED_COPPER_CHEST, WEATHERED_COPPER_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrConstants.Identifiers.OXIDIZED_COPPER_CHEST, OXIDIZED_COPPER_CHEST);
  }
}
