package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrInventoryBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrTrappedChestBlockEntity;
import noobanidus.mods.lootr.fabric.block.entity.LootrFabricBarrelBlockEntity;

public class ModBlockEntities {
  public static void registerBlockEntities() {
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.rl("lootr_chest"), LOOTR_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.rl("lootr_trapped_chest"), LOOTR_TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.rl("lootr_shulker"), LOOTR_SHULKER);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.rl("lootr_barrel"), LOOTR_BARREL);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrAPI.rl("lootr_inventory"), LOOTR_INVENTORY);
  }

  public static final BlockEntityType<LootrChestBlockEntity> LOOTR_CHEST = BlockEntityType.Builder.of(LootrChestBlockEntity::new, ModBlocks.CHEST).build(null);
  public static final BlockEntityType<LootrFabricBarrelBlockEntity> LOOTR_BARREL = BlockEntityType.Builder.of(LootrFabricBarrelBlockEntity::new, ModBlocks.BARREL).build(null);
  public static final BlockEntityType<LootrTrappedChestBlockEntity> LOOTR_TRAPPED_CHEST = BlockEntityType.Builder.of(LootrTrappedChestBlockEntity::new, ModBlocks.TRAPPED_CHEST).build(null);
  public static final BlockEntityType<LootrShulkerBlockEntity> LOOTR_SHULKER = BlockEntityType.Builder.of(LootrShulkerBlockEntity::new, ModBlocks.SHULKER).build(null);
  public static final BlockEntityType<LootrInventoryBlockEntity> LOOTR_INVENTORY = BlockEntityType.Builder.of(LootrInventoryBlockEntity::new, ModBlocks.INVENTORY).build(null);


}
