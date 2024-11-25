package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrInventoryBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrTrappedChestBlockEntity;
import noobanidus.mods.lootr.neoforge.block.entity.LootrNeoForgeBarrelBlockEntity;

@SuppressWarnings("DataFlowIssue")
public class ModBlockEntities {
  private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.MODID);

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrChestBlockEntity>> LOOTR_CHEST = REGISTER.register("lootr_chest", () -> new BlockEntityType<>(LootrChestBlockEntity::new, LootrRegistry.getChestBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrTrappedChestBlockEntity>> LOOTR_TRAPPED_CHEST = REGISTER.register("lootr_trapped_chest", () -> new BlockEntityType<>(LootrTrappedChestBlockEntity::new, LootrRegistry.getTrappedChestBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrNeoForgeBarrelBlockEntity>> LOOTR_BARREL = REGISTER.register("lootr_barrel", () -> new BlockEntityType<>(LootrNeoForgeBarrelBlockEntity::new, LootrRegistry.getBarrelBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrInventoryBlockEntity>> LOOTR_INVENTORY = REGISTER.register("lootr_inventory", () -> new BlockEntityType<>(LootrInventoryBlockEntity::new, LootrRegistry.getInventoryBlock()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrShulkerBlockEntity>> LOOTR_SHULKER = REGISTER.register("lootr_shulker", () -> new BlockEntityType<>(LootrShulkerBlockEntity::new, LootrRegistry.getShulkerBlock()));


}
