package noobanidus.mods.lootr.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.block.entity.*;

public class ModBlockEntities {
  private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.MODID);

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrChestBlockEntity>> LOOTR_CHEST = REGISTER.register("lootr_chest", () -> BlockEntityType.Builder.of(LootrChestBlockEntity::new, LootrRegistry.getChestBlock()).build(null));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrTrappedChestBlockEntity>> LOOTR_TRAPPED_CHEST = REGISTER.register("lootr_trapped_chest", () -> BlockEntityType.Builder.of(LootrTrappedChestBlockEntity::new, LootrRegistry.getTrappedChestBlock()).build(null));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrBarrelBlockEntity>> LOOTR_BARREL = REGISTER.register("lootr_barrel", () -> BlockEntityType.Builder.of(LootrBarrelBlockEntity::new, LootrRegistry.getBarrelBlock()).build(null));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrInventoryBlockEntity>> LOOTR_INVENTORY = REGISTER.register("lootr_inventory", () -> BlockEntityType.Builder.of(LootrInventoryBlockEntity::new, LootrRegistry.getInventoryBlock()).build(null));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootrShulkerBlockEntity>> LOOTR_SHULKER = REGISTER.register("lootr_shulker", () -> BlockEntityType.Builder.of(LootrShulkerBlockEntity::new, LootrRegistry.getShulkerBlock()).build(null));


}
