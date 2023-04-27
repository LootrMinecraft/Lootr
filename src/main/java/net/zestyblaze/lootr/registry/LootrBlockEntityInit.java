package net.zestyblaze.lootr.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.blocks.entities.*;
import net.zestyblaze.lootr.config.LootrModConfig;

public class LootrBlockEntityInit {
    public static final BlockEntityType<LootrChestBlockEntity> SPECIAL_LOOT_CHEST = FabricBlockEntityTypeBuilder.create(LootrChestBlockEntity::new, LootrBlockInit.CHEST).build(null);
    public static final BlockEntityType<LootrBarrelBlockEntity> SPECIAL_LOOT_BARREL = FabricBlockEntityTypeBuilder.create(LootrBarrelBlockEntity::new, LootrBlockInit.BARREL).build(null);
    public static final BlockEntityType<LootrTrappedChestBlockEntity> SPECIAL_TRAPPED_LOOT_CHEST = FabricBlockEntityTypeBuilder.create(LootrTrappedChestBlockEntity::new, LootrBlockInit.TRAPPED_CHEST).build(null);
    public static final BlockEntityType<LootrShulkerBlockEntity> SPECIAL_LOOT_SHULKER = FabricBlockEntityTypeBuilder.create(LootrShulkerBlockEntity::new, LootrBlockInit.SHULKER).build(null);
    public static final BlockEntityType<LootrInventoryBlockEntity> SPECIAL_LOOT_INVENTORY = FabricBlockEntityTypeBuilder.create(LootrInventoryBlockEntity::new, LootrBlockInit.INVENTORY).build(null);

    public static void registerBlockEntities() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_chest"), SPECIAL_LOOT_CHEST);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_trapped_loot_chest"), SPECIAL_TRAPPED_LOOT_CHEST);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_shulker"), SPECIAL_LOOT_SHULKER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_barrel"), SPECIAL_LOOT_BARREL);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_inventory"), SPECIAL_LOOT_INVENTORY);
    }
}
