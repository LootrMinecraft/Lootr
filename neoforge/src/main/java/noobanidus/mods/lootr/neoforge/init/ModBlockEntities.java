package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.annotation.MigrateName;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.*;

public class ModBlockEntities {
  private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.MODID);

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }

  @MigrateName(value = "chest", in = "26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrChestBlockEntity>> LOOTR_CHEST = REGISTER.register(LootrConstants.LOOTR_CHEST.getPath(), () -> new BlockEntityType<>(LootrChestBlockEntity::new, LootrRegistry.getChestBlock()));
  @MigrateName(value = "trapped_chest", in = "26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrTrappedChestBlockEntity>> LOOTR_TRAPPED_CHEST = REGISTER.register(LootrConstants.LOOTR_TRAPPED_CHEST.getPath(), () -> new BlockEntityType<>(LootrTrappedChestBlockEntity::new, LootrRegistry.getTrappedChestBlock()));
  @MigrateName(value = "barrel", in = "26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrBarrelBlockEntity>> LOOTR_BARREL = REGISTER.register(LootrConstants.LOOTR_BARREL.getPath(), () -> new BlockEntityType<>(LootrBarrelBlockEntity::new, LootrRegistry.getBarrelBlock()));
  @MigrateName(value = "inventory", in = "26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrInventoryBlockEntity>> LOOTR_INVENTORY = REGISTER.register(LootrConstants.LOOTR_INVENTORY.getPath(), () -> new BlockEntityType<>(LootrInventoryBlockEntity::new, LootrRegistry.getInventoryBlock()));
  @MigrateName(value = "shulker_box", in = "26")
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrShulkerBlockEntity>> LOOTR_SHULKER = REGISTER.register(LootrConstants.LOOTR_SHULKER.getPath(), () -> new BlockEntityType<>(LootrShulkerBlockEntity::new, LootrRegistry.getShulkerBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrBrushableBlockEntity>> LOOTR_BRUSHABLE_BLOCK = REGISTER.register(LootrConstants.BRUSHABLE_BLOCK.getPath(), () -> new BlockEntityType<>(LootrBrushableBlockEntity::new, ModBlocks.SUSPICIOUS_GRAVEL.get(), ModBlocks.SUSPICIOUS_SAND.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrDecoratedPotBlockEntity>> LOOTR_DECORATED_POT = REGISTER.register(LootrConstants.DECORATED_POT.getPath(), () -> new BlockEntityType<>(LootrDecoratedPotBlockEntity::new, ModBlocks.DECORATED_POT.get()));
}
