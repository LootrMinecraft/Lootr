package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.annotation.MigrateName;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.block.*;
import noobanidus.mods.lootr.neoforge.block.LootrNeoForgeBarrelBlock;
import noobanidus.mods.lootr.neoforge.block.LootrNeoForgeBrushableBlock;


public class ModBlocks {
  private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK, LootrAPI.MODID);

  @MigrateName(value="barrel", in="26")
  public static final DeferredHolder<Block, LootrBarrelBlock> BARREL = REGISTER.register(LootrConstants.LOOTR_BARREL.getPath(), () -> new LootrNeoForgeBarrelBlock(LootrConstants.BARREL_PROPERTIES));
  @MigrateName(value="chest", in="26")
  public static final DeferredHolder<Block, LootrChestBlock> CHEST = REGISTER.register(LootrConstants.LOOTR_CHEST.getPath(), () -> new LootrChestBlock(LootrConstants.CHEST_PROPERTIES));
  @MigrateName(value="trapped_chest", in="26")
  public static final DeferredHolder<Block, LootrTrappedChestBlock> TRAPPED_CHEST = REGISTER.register(LootrConstants.LOOTR_TRAPPED_CHEST.getPath(), () -> new LootrTrappedChestBlock(LootrConstants.TRAPPED_CHEST_PROPERTIES));
  @MigrateName(value="inventory", in="26")
  public static final DeferredHolder<Block, LootrInventoryBlock> INVENTORY = REGISTER.register(LootrConstants.LOOTR_INVENTORY.getPath(), () -> new LootrInventoryBlock(LootrConstants.INVENTORY_PROPERTIES));
  @MigrateName(value="shulker_box", in="26")
  public static final DeferredHolder<Block, LootrShulkerBlock> SHULKER = REGISTER.register(LootrConstants.LOOTR_SHULKER.getPath(), () -> new LootrShulkerBlock(LootrConstants.SHULKER_BOX_PROPERTIES));
  public static final DeferredHolder<Block, LootrBrushableBlock> SUSPICIOUS_SAND = REGISTER.register(LootrConstants.SUSPICIOUS_SAND.getPath(), () -> new LootrNeoForgeBrushableBlock(Blocks.SAND, ((BrushableBlock)Blocks.SUSPICIOUS_SAND).getBrushSound(), ((BrushableBlock)Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.SUSPICIOUS_SAND_PROPERTIES));
  public static final DeferredHolder<Block, LootrBrushableBlock> SUSPICIOUS_GRAVEL = REGISTER.register(LootrConstants.SUSPICIOUS_GRAVEL.getPath(), () -> new LootrNeoForgeBrushableBlock(Blocks.GRAVEL, ((BrushableBlock)Blocks.SUSPICIOUS_GRAVEL).getBrushSound(), ((BrushableBlock)Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrConstants.SUSPICIOUS_GRAVEL_PROPERTIES));
  public static final DeferredHolder<Block, LootrDecoratedPotBlock> DECORATED_POT = REGISTER.register(LootrConstants.DECORATED_POT.getPath(), () -> new LootrDecoratedPotBlock(LootrConstants.DECORATED_POT_PROPERTIES));

  public static final DeferredHolder<Block, Block> TROPHY = REGISTER.register(LootrConstants.TROPHY.getPath(), () -> new TrophyBlock(LootrConstants.TROPHY_PROPERTIES));


  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
