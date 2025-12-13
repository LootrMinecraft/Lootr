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
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import noobanidus.mods.lootr.common.block.*;
import noobanidus.mods.lootr.neoforge.block.LootrNeoForgeBarrelBlock;
import noobanidus.mods.lootr.neoforge.block.LootrNeoForgeBrushableBlock;


public class ModBlocks {
  private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK, LootrAPI.MODID);

  @MigrateName(value="barrel", in="26")
  public static final DeferredHolder<Block, LootrBarrelBlock> BARREL = REGISTER.register(LootrProperties.LOOTR_BARREL.getPath(), () -> new LootrNeoForgeBarrelBlock(LootrProperties.BARREL_PROPERTIES));
  @MigrateName(value="chest", in="26")
  public static final DeferredHolder<Block, LootrChestBlock> CHEST = REGISTER.register("lootr_chest", () -> new LootrChestBlock(LootrProperties.CHEST_PROPERTIES));
  @MigrateName(value="trapped_chest", in="26")
  public static final DeferredHolder<Block, LootrTrappedChestBlock> TRAPPED_CHEST = REGISTER.register("lootr_trapped_chest", () -> new LootrTrappedChestBlock(LootrProperties.TRAPPED_CHEST_PROPERTIES));
  @MigrateName(value="inventory", in="26")
  public static final DeferredHolder<Block, LootrInventoryBlock> INVENTORY = REGISTER.register("lootr_inventory", () -> new LootrInventoryBlock(LootrProperties.INVENTORY_PROPERTIES));

  public static final DeferredHolder<Block, Block> TROPHY = REGISTER.register("trophy", () -> new TrophyBlock(LootrProperties.TROPHY_PROPERTIES));


  @MigrateName(value="shulker_box", in="26")
  public static final DeferredHolder<Block, LootrShulkerBlock> SHULKER = REGISTER.register("lootr_shulker", () -> new LootrShulkerBlock(LootrProperties.SHULKER_BOX_PROPERTIES));
  public static final DeferredHolder<Block, LootrBrushableBlock> SUSPICIOUS_SAND = REGISTER.register("suspicious_sand", () -> new LootrNeoForgeBrushableBlock(((BrushableBlock)Blocks.SUSPICIOUS_SAND).getBrushSound(), ((BrushableBlock)Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrProperties.SUSPICIOUS_SAND_PROPERTIES));
  public static final DeferredHolder<Block, LootrBrushableBlock> SUSPICIOUS_GRAVEL = REGISTER.register("suspicious_gravel", () -> new LootrNeoForgeBrushableBlock(((BrushableBlock)Blocks.SUSPICIOUS_GRAVEL).getBrushSound(), ((BrushableBlock)Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrProperties.SUSPICIOUS_GRAVEL_PROPERTIES));

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
