package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.annotation.MigrateName;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.*;
import noobanidus.mods.lootr.neoforge.block.entity.LootrNeoForgeBarrelBlockEntity;
import noobanidus.mods.lootr.neoforge.block.entity.LootrNeoForgeBrushableBlockEntity;

@SuppressWarnings("DataFlowIssue")
public class ModBlockEntities {
  private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.MODID);

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }

  @MigrateName(value="chest", in="26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrChestBlockEntity>> LOOTR_CHEST = REGISTER.register(LootrConstants.LOOTR_CHEST.getPath(), () -> BlockEntityType.Builder.of(LootrChestBlockEntity::new, LootrRegistry.getChestBlock()).build(null));
  @MigrateName(value="trapped_chest", in="26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrTrappedChestBlockEntity>> LOOTR_TRAPPED_CHEST = REGISTER.register(LootrConstants.LOOTR_TRAPPED_CHEST.getPath(), () -> BlockEntityType.Builder.of(LootrTrappedChestBlockEntity::new, LootrRegistry.getTrappedChestBlock()).build(null));
  @MigrateName(value="barrel", in="26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrNeoForgeBarrelBlockEntity>> LOOTR_BARREL = REGISTER.register(LootrConstants.LOOTR_BARREL.getPath(), () -> BlockEntityType.Builder.of(LootrNeoForgeBarrelBlockEntity::new, LootrRegistry.getBarrelBlock()).build(null));
  @MigrateName(value="inventory", in="26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrInventoryBlockEntity>> LOOTR_INVENTORY = REGISTER.register(LootrConstants.LOOTR_INVENTORY.getPath(), () -> BlockEntityType.Builder.of(LootrInventoryBlockEntity::new, LootrRegistry.getInventoryBlock()).build(null));
  @MigrateName(value="shulker_box", in="26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrShulkerBlockEntity>> LOOTR_SHULKER = REGISTER.register(LootrConstants.LOOTR_SHULKER.getPath(), () -> BlockEntityType.Builder.of(LootrShulkerBlockEntity::new, LootrRegistry.getShulkerBlock()).build(null));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrNeoForgeBrushableBlockEntity>> LOOTR_BRUSHABLE_BLOCK = REGISTER.register(LootrConstants.BRUSHABLE_BLOCK.getPath(), () -> BlockEntityType.Builder.of(LootrNeoForgeBrushableBlockEntity::new, ModBlocks.SUSPICIOUS_GRAVEL.get(), ModBlocks.SUSPICIOUS_SAND.get()).build(null));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrDecoratedPotBlockEntity>> LOOTR_DECORATED_POT = REGISTER.register(LootrConstants.DECORATED_POT.getPath(), () -> BlockEntityType.Builder.of(LootrDecoratedPotBlockEntity::new, ModBlocks.DECORATED_POT.get()).build(null));
}
