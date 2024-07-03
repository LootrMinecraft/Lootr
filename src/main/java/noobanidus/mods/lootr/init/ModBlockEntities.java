package noobanidus.mods.lootr.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.block.entities.LootrBarrelBlockEntity;
import noobanidus.mods.lootr.block.entities.LootrChestBlockEntity;
import noobanidus.mods.lootr.block.entities.LootrInventoryBlockEntity;
import noobanidus.mods.lootr.block.entities.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.block.entities.LootrTrappedChestBlockEntity;

public class ModBlockEntities {
    public static void registerBlockEntities() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "special_loot_chest"), SPECIAL_LOOT_CHEST);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "special_trapped_loot_chest"), SPECIAL_TRAPPED_LOOT_CHEST);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "special_loot_shulker"), SPECIAL_LOOT_SHULKER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "special_loot_barrel"), SPECIAL_LOOT_BARREL);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "special_loot_inventory"), SPECIAL_LOOT_INVENTORY);
    }

    public static final BlockEntityType<LootrChestBlockEntity> SPECIAL_LOOT_CHEST = BlockEntityType.Builder.of(LootrChestBlockEntity::new, ModBlocks.CHEST).build(null);
    public static final BlockEntityType<LootrBarrelBlockEntity> SPECIAL_LOOT_BARREL = BlockEntityType.Builder.of(LootrBarrelBlockEntity::new, ModBlocks.BARREL).build(null);
    public static final BlockEntityType<LootrTrappedChestBlockEntity> SPECIAL_TRAPPED_LOOT_CHEST = BlockEntityType.Builder.of(LootrTrappedChestBlockEntity::new, ModBlocks.TRAPPED_CHEST).build(null);
    public static final BlockEntityType<LootrShulkerBlockEntity> SPECIAL_LOOT_SHULKER = BlockEntityType.Builder.of(LootrShulkerBlockEntity::new, ModBlocks.SHULKER).build(null);
    public static final BlockEntityType<LootrInventoryBlockEntity> SPECIAL_LOOT_INVENTORY = BlockEntityType.Builder.of(LootrInventoryBlockEntity::new, ModBlocks.INVENTORY).build(null);


}
