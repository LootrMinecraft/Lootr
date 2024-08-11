package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.*;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;

public class ModBlocks {
  public static final LootrChestBlock CHEST = new LootrChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST).strength(2.5f));
  public static final LootrBarrelBlock BARREL = new LootrBarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).strength(2.5f).forceSolidOff());
  public static final LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRAPPED_CHEST).strength(2.5f));
  public static final LootrInventoryBlock INVENTORY = new LootrInventoryBlock(BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD));
  public static final Block TROPHY = new TrophyBlock(BlockBehaviour.Properties.of().strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15));
  private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (blockEntity instanceof LootrShulkerBlockEntity shulkerBlockEntity) {
      return shulkerBlockEntity.isClosed();
    } else {
      return false;
    }
  };
  public static final LootrShulkerBlock SHULKER = new LootrShulkerBlock(BlockBehaviour.Properties.of().strength(2.5f).dynamicShape().noOcclusion().isSuffocating(posPredicate).isViewBlocking(posPredicate));

  public static void registerBlocks() {
    Registry.register(BuiltInRegistries.BLOCK, LootrAPI.rl("lootr_chest"), CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrAPI.rl("lootr_barrel"), BARREL);
    Registry.register(BuiltInRegistries.BLOCK, LootrAPI.rl("lootr_trapped_chest"), TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK, LootrAPI.rl("lootr_shulker"), SHULKER);
    Registry.register(BuiltInRegistries.BLOCK, LootrAPI.rl("lootr_inventory"), INVENTORY);
    Registry.register(BuiltInRegistries.BLOCK, LootrAPI.rl("trophy"), TROPHY);
  }
}
