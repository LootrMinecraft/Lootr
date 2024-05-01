package net.zestyblaze.lootr.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.block.entities.*;

public class ModBlockEntities {
  public static void registerBlockEntities() {
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_chest"), SPECIAL_LOOT_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_trapped_loot_chest"), SPECIAL_TRAPPED_LOOT_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_shulker"), SPECIAL_LOOT_SHULKER);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_barrel"), SPECIAL_LOOT_BARREL);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_inventory"), SPECIAL_LOOT_INVENTORY);
  }  public static final BlockEntityType<LootrChestBlockEntity> SPECIAL_LOOT_CHEST = FabricBlockEntityTypeBuilder.create(LootrChestBlockEntity::new, ModBlocks.CHEST).build(null);
  public static final BlockEntityType<LootrBarrelBlockEntity> SPECIAL_LOOT_BARREL = FabricBlockEntityTypeBuilder.create(LootrBarrelBlockEntity::new, ModBlocks.BARREL).build(null);
  public static final BlockEntityType<LootrTrappedChestBlockEntity> SPECIAL_TRAPPED_LOOT_CHEST = FabricBlockEntityTypeBuilder.create(LootrTrappedChestBlockEntity::new, ModBlocks.TRAPPED_CHEST).build(null);
  public static final BlockEntityType<LootrShulkerBlockEntity> SPECIAL_LOOT_SHULKER = FabricBlockEntityTypeBuilder.create(LootrShulkerBlockEntity::new, ModBlocks.SHULKER).build(null);
  public static final BlockEntityType<LootrInventoryBlockEntity> SPECIAL_LOOT_INVENTORY = FabricBlockEntityTypeBuilder.create(LootrInventoryBlockEntity::new, ModBlocks.INVENTORY).build(null);


}
