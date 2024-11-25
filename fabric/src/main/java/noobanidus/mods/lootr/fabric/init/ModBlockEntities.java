package noobanidus.mods.lootr.fabric.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
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

  public static final BlockEntityType<LootrChestBlockEntity> LOOTR_CHEST = FabricBlockEntityTypeBuilder.create(LootrChestBlockEntity::new, ModBlocks.CHEST).build(null);
  public static final BlockEntityType<LootrFabricBarrelBlockEntity> LOOTR_BARREL = FabricBlockEntityTypeBuilder.create(LootrFabricBarrelBlockEntity::new, ModBlocks.BARREL).build(null);
  public static final BlockEntityType<LootrTrappedChestBlockEntity> LOOTR_TRAPPED_CHEST = FabricBlockEntityTypeBuilder.create(LootrTrappedChestBlockEntity::new, ModBlocks.TRAPPED_CHEST).build(null);
  public static final BlockEntityType<LootrShulkerBlockEntity> LOOTR_SHULKER = FabricBlockEntityTypeBuilder.create(LootrShulkerBlockEntity::new, ModBlocks.SHULKER).build(null);
  public static final BlockEntityType<LootrInventoryBlockEntity> LOOTR_INVENTORY = FabricBlockEntityTypeBuilder.create(LootrInventoryBlockEntity::new, ModBlocks.INVENTORY).build(null);


}
