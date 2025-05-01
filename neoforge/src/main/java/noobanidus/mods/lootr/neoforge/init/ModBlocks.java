package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.*;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.neoforge.block.LootrNeoForgeBarrelBlock;


public class ModBlocks {
  private static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(LootrAPI.MODID);
  public static final DeferredBlock<LootrBarrelBlock> BARREL = REGISTER.registerBlock(LootrRegistry.BARREL.getPath(), LootrNeoForgeBarrelBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST).strength(2.5f).setId(LootrRegistry.BARREL_BLOCK_KEY));
  public static final DeferredBlock<LootrChestBlock> CHEST = REGISTER.registerBlock(LootrRegistry.CHEST.getPath(), LootrChestBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).strength(2.5f).setId(LootrRegistry.CHEST_BLOCK_KEY));
  public static final DeferredBlock<LootrTrappedChestBlock> TRAPPED_CHEST = REGISTER.registerBlock(LootrRegistry.TRAPPED_CHEST.getPath(), LootrTrappedChestBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.TRAPPED_CHEST).strength(2.5f).setId(LootrRegistry.TRAPPED_CHEST_BLOCK_KEY));
  public static final DeferredBlock<LootrInventoryBlock> INVENTORY = REGISTER.registerBlock(LootrRegistry.INVENTORY.getPath(), LootrInventoryBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST).strength(2.5f).sound(SoundType.WOOD).setId(LootrRegistry.INVENTORY_BLOCK_KEY));
  public static final DeferredBlock<Block> TROPHY = REGISTER.registerBlock(LootrRegistry.TROPHY.getPath(), TrophyBlock::new, BlockBehaviour.Properties.of().strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15).setId(LootrRegistry.TROPHY_BLOCK_KEY));
  private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
    BlockEntity blockentity = level.getBlockEntity(pos);
    if (blockentity instanceof LootrShulkerBlockEntity shulkerboxblockentity) {
      return shulkerboxblockentity.isClosed();
    } else {
      return false;
    }
  };
  public static final DeferredBlock<LootrShulkerBlock> SHULKER = REGISTER.registerBlock(LootrRegistry.SHULKER.getPath(), LootrShulkerBlock::new, BlockBehaviour.Properties.of().strength(2.5f).dynamicShape().noOcclusion().forceSolidOn().pushReaction(PushReaction.DESTROY).isSuffocating(posPredicate).isViewBlocking(posPredicate).setId(LootrRegistry.SHULKER_BLOCK_KEY));

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
