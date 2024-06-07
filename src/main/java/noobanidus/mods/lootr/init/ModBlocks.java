package noobanidus.mods.lootr.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.block.*;
import noobanidus.mods.lootr.block.entities.LootrShulkerBlockEntity;


public class ModBlocks {
  private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK, LootrAPI.MODID);

  private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
    BlockEntity blockentity = level.getBlockEntity(pos);
    if (blockentity instanceof LootrShulkerBlockEntity shulkerboxblockentity) {
      return shulkerboxblockentity.isClosed();
    } else {
      return false;
    }
  };

  public static final DeferredHolder<Block, LootrBarrelBlock> BARREL = REGISTER.register("lootr_barrel", () -> new LootrBarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST).strength(2.5f)));
  public static final DeferredHolder<Block, LootrChestBlock> CHEST = REGISTER.register("lootr_chest", () -> new LootrChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).strength(2.5f)));
  public static final DeferredHolder<Block, LootrTrappedChestBlock> TRAPPED_CHEST = REGISTER.register("lootr_trapped_chest", () -> new LootrTrappedChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRAPPED_CHEST).strength(2.5f)));
  public static final DeferredHolder<Block, LootrInventoryBlock> INVENTORY = REGISTER.register("lootr_inventory", () -> new LootrInventoryBlock(Block.Properties.of().strength(2.5f).sound(SoundType.WOOD)));
  public static final DeferredHolder<Block, Block> TROPHY = REGISTER.register("trophy", () -> new TrophyBlock(Block.Properties.of().strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15)));

  public static final DeferredHolder<Block, LootrShulkerBlock> SHULKER = REGISTER.register("lootr_shulker", () -> new LootrShulkerBlock(Block.Properties.of().strength(2.5f).dynamicShape().noOcclusion().forceSolidOn().pushReaction(PushReaction.DESTROY).isSuffocating(posPredicate).isViewBlocking(posPredicate)));

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
