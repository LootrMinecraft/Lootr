package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.block.*;


public class ModBlocks {
  private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK, LootrAPI.MODID);

  public static final DeferredHolder<Block, LootrBarrelBlock> BARREL = REGISTER.register(LootrConstants.Identifiers.BARREL.getPath(), () -> new LootrBarrelBlock(LootrConstants.BlockProperties.BARREL));
  public static final DeferredHolder<Block, LootrChestBlock> CHEST = REGISTER.register(LootrConstants.Identifiers.CHEST.getPath(), () -> new LootrChestBlock(LootrConstants.BlockProperties.CHEST));
  public static final DeferredHolder<Block, LootrCopperChestBlock> COPPER_CHEST = REGISTER.register(LootrConstants.Identifiers.COPPER_CHEST.getPath(), () -> new LootrCopperChestBlock(LootrCopperChestBlock.COPPER, LootrConstants.BlockProperties.COPPER_CHEST));
  public static final DeferredHolder<Block, LootrCopperChestBlock> EXPOSED_COPPER_CHEST = REGISTER.register(LootrConstants.Identifiers.EXPOSED_COPPER_CHEST.getPath(), () -> new LootrCopperChestBlock(LootrCopperChestBlock.EXPOSED, LootrConstants.BlockProperties.EXPOSED_COPPER_CHEST));
  public static final DeferredHolder<Block, LootrCopperChestBlock> WEATHERED_COPPER_CHEST = REGISTER.register(LootrConstants.Identifiers.WEATHERED_COPPER_CHEST.getPath(), () -> new LootrCopperChestBlock(LootrCopperChestBlock.WEATHERED, LootrConstants.BlockProperties.WEATHERED_COPPER_CHEST));
  public static final DeferredHolder<Block, LootrCopperChestBlock> OXIDIZED_COPPER_CHEST = REGISTER.register(LootrConstants.Identifiers.OXIDIZED_COPPER_CHEST.getPath(), () -> new LootrCopperChestBlock(LootrCopperChestBlock.OXIDIZED, LootrConstants.BlockProperties.OXIDIZED_COPPER_CHEST));
  public static final DeferredHolder<Block, LootrTrappedChestBlock> TRAPPED_CHEST = REGISTER.register(LootrConstants.Identifiers.TRAPPED_CHEST.getPath(), () -> new LootrTrappedChestBlock(LootrConstants.BlockProperties.TRAPPED_CHEST));
  public static final DeferredHolder<Block, LootrShulkerBoxBlock> SHULKER_BOX = REGISTER.register(LootrConstants.Identifiers.SHULKER_BOX.getPath(), () -> new LootrShulkerBoxBlock(LootrConstants.BlockProperties.SHULKER_BOX));
  public static final DeferredHolder<Block, LootrBrushableBlock> SUSPICIOUS_SAND = REGISTER.register(LootrConstants.Identifiers.SUSPICIOUS_SAND.getPath(), () -> new LootrBrushableBlock(Blocks.SAND, ((BrushableBlock) Blocks.SUSPICIOUS_SAND).getBrushSound(), ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.BlockProperties.SUSPICIOUS_SAND));
  public static final DeferredHolder<Block, LootrBrushableBlock> SUSPICIOUS_GRAVEL = REGISTER.register(LootrConstants.Identifiers.SUSPICIOUS_GRAVEL.getPath(), () -> new LootrBrushableBlock(Blocks.GRAVEL, ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushSound(), ((BrushableBlock) Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.BlockProperties.SUSPICIOUS_GRAVEL));
  public static final DeferredHolder<Block, LootrDecoratedPotBlock> DECORATED_POT = REGISTER.register(LootrConstants.Identifiers.DECORATED_POT.getPath(), () -> new LootrDecoratedPotBlock(LootrConstants.BlockProperties.DECORATED_POT));

  public static final DeferredHolder<Block, Block> TROPHY = REGISTER.register(LootrConstants.Identifiers.TROPHY.getPath(), () -> new TrophyBlock(LootrConstants.BlockProperties.TROPHY));

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
