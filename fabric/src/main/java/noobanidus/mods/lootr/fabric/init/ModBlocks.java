package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import noobanidus.mods.lootr.common.block.*;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.fabric.block.LootrFabricBarrelBlock;
import noobanidus.mods.lootr.fabric.block.LootrFabricBrushableBlock;

public class ModBlocks {
  public static final LootrChestBlock CHEST = new LootrChestBlock(LootrProperties.CHEST_PROPERTIES);
  public static final LootrFabricBarrelBlock BARREL = new LootrFabricBarrelBlock(LootrProperties.BARREL_PROPERTIES);
  public static final LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(LootrProperties.TRAPPED_CHEST_PROPERTIES);
  public static final LootrInventoryBlock INVENTORY = new LootrInventoryBlock(LootrProperties.INVENTORY_PROPERTIES);
  public static final Block TROPHY = new TrophyBlock(LootrProperties.TROPHY_PROPERTIES);
  public static final LootrShulkerBlock SHULKER = new LootrShulkerBlock(LootrProperties.SHULKER_BOX_PROPERTIES);

  public static final LootrBrushableBlock SUSPICIOUS_SAND = new LootrFabricBrushableBlock(((BrushableBlock)Blocks.SUSPICIOUS_SAND).getBrushSound(), ((BrushableBlock)Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrProperties.SUSPICIOUS_SAND_PROPERTIES);
  public static final LootrBrushableBlock SUSPICIOUS_GRAVEL = new LootrFabricBrushableBlock(((BrushableBlock)Blocks.SUSPICIOUS_GRAVEL).getBrushSound(), ((BrushableBlock)Blocks.SUSPICIOUS_GRAVEL).getBrushCompletedSound(), LootrProperties.SUSPICIOUS_GRAVEL_PROPERTIES);

  public static void registerBlocks() {
    Registry.register(BuiltInRegistries.BLOCK, LootrProperties.LOOTR_CHEST, CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrProperties.LOOTR_BARREL, BARREL);
    Registry.register(BuiltInRegistries.BLOCK, LootrProperties.LOOTR_TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrProperties.LOOTR_SHULKER, SHULKER);
    Registry.register(BuiltInRegistries.BLOCK, LootrProperties.LOOTR_INVENTORY, INVENTORY);
    Registry.register(BuiltInRegistries.BLOCK, LootrProperties.SUSPICIOUS_SAND, SUSPICIOUS_SAND);
    Registry.register(BuiltInRegistries.BLOCK, LootrProperties.SUSPICIOUS_GRAVEL, SUSPICIOUS_GRAVEL);
    Registry.register(BuiltInRegistries.BLOCK, LootrProperties.TROPHY, TROPHY);
  }
}
