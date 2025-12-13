package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import noobanidus.mods.lootr.common.block.entity.*;
import noobanidus.mods.lootr.fabric.block.entity.LootrFabricBarrelBlockEntity;
import noobanidus.mods.lootr.fabric.block.entity.LootrFabricBrushableBlockEntity;

public class ModBlockEntities {
  public static void registerBlockEntities() {
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrProperties.LOOTR_CHEST, LOOTR_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrProperties.LOOTR_TRAPPED_CHEST, LOOTR_TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrProperties.LOOTR_SHULKER, LOOTR_SHULKER);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrProperties.LOOTR_BARREL, LOOTR_BARREL);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrProperties.LOOTR_INVENTORY, LOOTR_INVENTORY);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrProperties.BRUSHABLE_BLOCK, LOOTR_BRUSHABLE_BLOCK);
  }

  public static final BlockEntityType<LootrChestBlockEntity> LOOTR_CHEST = BlockEntityType.Builder.of(LootrChestBlockEntity::new, ModBlocks.CHEST).build(null);
  public static final BlockEntityType<LootrFabricBarrelBlockEntity> LOOTR_BARREL = BlockEntityType.Builder.of(LootrFabricBarrelBlockEntity::new, ModBlocks.BARREL).build(null);
  public static final BlockEntityType<LootrTrappedChestBlockEntity> LOOTR_TRAPPED_CHEST = BlockEntityType.Builder.of(LootrTrappedChestBlockEntity::new, ModBlocks.TRAPPED_CHEST).build(null);
  public static final BlockEntityType<LootrShulkerBlockEntity> LOOTR_SHULKER = BlockEntityType.Builder.of(LootrShulkerBlockEntity::new, ModBlocks.SHULKER).build(null);
  public static final BlockEntityType<LootrInventoryBlockEntity> LOOTR_INVENTORY = BlockEntityType.Builder.of(LootrInventoryBlockEntity::new, ModBlocks.INVENTORY).build(null);
  public static final BlockEntityType<LootrFabricBrushableBlockEntity> LOOTR_BRUSHABLE_BLOCK = BlockEntityType.Builder.of(LootrFabricBrushableBlockEntity::new, ModBlocks.SUSPICIOUS_GRAVEL, ModBlocks.SUSPICIOUS_SAND).build(null);
}
