package noobanidus.mods.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.blocks.entities.*;
import noobanidus.mods.lootr.util.PlatformUtils;

import java.util.function.Supplier;

public class LootrBlockEntityInit {
    private static BlockEntityType<LootrChestBlockEntity> lootChestEntity;
    public static final Supplier<BlockEntityType<LootrChestBlockEntity>> LOOT_CHEST_ENTITY_PROVIDER = () -> lootChestEntity;
    private static BlockEntityType<LootrTrappedChestBlockEntity> trappedLootChestEntity;
    public static final Supplier<BlockEntityType<LootrTrappedChestBlockEntity>> TRAPPED_LOOT_CHEST_ENTITY_PROVIDER = () -> trappedLootChestEntity;
    private static BlockEntityType<LootrBarrelBlockEntity> lootBarrelEntity;
    public static final Supplier<BlockEntityType<LootrBarrelBlockEntity>> LOOT_BARREL_ENTITY_PROVIDER = () -> lootBarrelEntity;
    private static BlockEntityType<LootrShulkerBlockEntity> lootShulkerEntity;
    public static final Supplier<BlockEntityType<LootrShulkerBlockEntity>> LOOT_SHULKER_ENTITY_PROVIDER = () -> lootShulkerEntity;
    private static BlockEntityType<LootrInventoryBlockEntity> lootInventoryEntity;
    public static final Supplier<BlockEntityType<LootrInventoryBlockEntity>> LOOT_INVENTORY_ENTITY_PROVIDER = () -> lootInventoryEntity;

    public static void registerBlockEntities() {
        lootChestEntity = PlatformUtils.createBlockEntityType(LootrChestBlockEntity::new, LootrBlockInit.CHEST.get());
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_chest"), lootChestEntity);
        trappedLootChestEntity = PlatformUtils.createBlockEntityType(LootrTrappedChestBlockEntity::new, LootrBlockInit.TRAPPED_CHEST.get());
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_trapped_loot_chest"), trappedLootChestEntity);
        lootBarrelEntity = PlatformUtils.createBlockEntityType(LootrBarrelBlockEntity::new, LootrBlockInit.BARREL.get());
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_barrel"), lootBarrelEntity);
        lootShulkerEntity = PlatformUtils.createBlockEntityType(LootrShulkerBlockEntity::new, LootrBlockInit.SHULKER.get());
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_shulker"), lootShulkerEntity);
        lootInventoryEntity = PlatformUtils.createBlockEntityType(LootrInventoryBlockEntity::new, LootrBlockInit.INVENTORY.get());
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_inventory"), lootInventoryEntity);
    }
}
