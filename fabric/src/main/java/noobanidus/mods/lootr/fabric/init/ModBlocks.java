package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.*;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.fabric.block.LootrFabricBarrelBlock;

public class ModBlocks {
  public static final LootrChestBlock CHEST = new LootrChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST).strength(2.5f).setId(LootrRegistry.CHEST_BLOCK_KEY));
  @SuppressWarnings("deprecation")
  public static final LootrFabricBarrelBlock BARREL = new LootrFabricBarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).strength(2.5f).forceSolidOff().setId(LootrRegistry.BARREL_BLOCK_KEY));
  public static final LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRAPPED_CHEST).strength(2.5f).setId(LootrRegistry.TRAPPED_CHEST_BLOCK_KEY));
  public static final LootrInventoryBlock INVENTORY = new LootrInventoryBlock(BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD).setId(LootrRegistry.INVENTORY_BLOCK_KEY));
  public static final Block TROPHY = new TrophyBlock(BlockBehaviour.Properties.of().strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15).setId(LootrRegistry.TROPHY_BLOCK_KEY));
  private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (blockEntity instanceof LootrShulkerBlockEntity shulkerBlockEntity) {
      return shulkerBlockEntity.isClosed();
    } else {
      return false;
    }
  };
  public static final LootrShulkerBlock SHULKER = new LootrShulkerBlock(BlockBehaviour.Properties.of().strength(2.5f).dynamicShape().noOcclusion().isSuffocating(posPredicate).isViewBlocking(posPredicate).setId(LootrRegistry.SHULKER_BLOCK_KEY));

  public static void registerBlocks() {
    Registry.register(BuiltInRegistries.BLOCK, LootrRegistry.CHEST, CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrRegistry.BARREL, BARREL);
    Registry.register(BuiltInRegistries.BLOCK, LootrRegistry.TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrRegistry.INVENTORY, INVENTORY);
    Registry.register(BuiltInRegistries.BLOCK, LootrRegistry.TROPHY, TROPHY);
    Registry.register(BuiltInRegistries.BLOCK, LootrRegistry.SHULKER, SHULKER);
  }
}
