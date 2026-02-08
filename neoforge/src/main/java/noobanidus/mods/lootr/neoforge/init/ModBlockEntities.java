package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.*;

public class ModBlockEntities {
  private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.MODID);

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrChestBlockEntity>> CHEST = REGISTER.register(LootrConstants.CHEST.getPath(), () -> new BlockEntityType<>(LootrChestBlockEntity::new, LootrRegistry.getChestBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrTrappedChestBlockEntity>> TRAPPED_CHEST = REGISTER.register(LootrConstants.TRAPPED_CHEST.getPath(), () -> new BlockEntityType<>(LootrTrappedChestBlockEntity::new, LootrRegistry.getTrappedChestBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrBarrelBlockEntity>> BARREL = REGISTER.register(LootrConstants.BARREL.getPath(), () -> new BlockEntityType<>(LootrBarrelBlockEntity::new, LootrRegistry.getBarrelBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrInventoryBlockEntity>> INVENTORY = REGISTER.register(LootrConstants.INVENTORY.getPath(), () -> new BlockEntityType<>(LootrInventoryBlockEntity::new, LootrRegistry.getInventoryBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrShulkerBlockEntity>> SHULKER_BOX = REGISTER.register(LootrConstants.SHULKER_BOX.getPath(), () -> new BlockEntityType<>(LootrShulkerBlockEntity::new, LootrRegistry.getShulkerBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrBrushableBlockEntity>> LOOTR_BRUSHABLE_BLOCK = REGISTER.register(LootrConstants.BRUSHABLE_BLOCK.getPath(), () -> new BlockEntityType<>(LootrBrushableBlockEntity::new, ModBlocks.SUSPICIOUS_GRAVEL.get(), ModBlocks.SUSPICIOUS_SAND.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrDecoratedPotBlockEntity>> LOOTR_DECORATED_POT = REGISTER.register(LootrConstants.DECORATED_POT.getPath(), () -> new BlockEntityType<>(LootrDecoratedPotBlockEntity::new, ModBlocks.DECORATED_POT.get()));
}
