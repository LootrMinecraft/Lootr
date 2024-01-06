package noobanidus.mods.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.blocks.entities.*;
import noobanidus.mods.lootr.util.PlatformUtils;

public class LootrBlockEntityInit {
    public static final BlockEntityType<LootrChestBlockEntity> SPECIAL_LOOT_CHEST = PlatformUtils.createBlockEntityType(LootrChestBlockEntity::new, LootrBlockInit.CHEST);
    public static final BlockEntityType<LootrBarrelBlockEntity> SPECIAL_LOOT_BARREL = PlatformUtils.createBlockEntityType(LootrBarrelBlockEntity::new, LootrBlockInit.BARREL);
    public static final BlockEntityType<LootrTrappedChestBlockEntity> SPECIAL_TRAPPED_LOOT_CHEST = PlatformUtils.createBlockEntityType(LootrTrappedChestBlockEntity::new, LootrBlockInit.TRAPPED_CHEST);
    public static final BlockEntityType<LootrShulkerBlockEntity> SPECIAL_LOOT_SHULKER = PlatformUtils.createBlockEntityType(LootrShulkerBlockEntity::new, LootrBlockInit.SHULKER);
    public static final BlockEntityType<LootrInventoryBlockEntity> SPECIAL_LOOT_INVENTORY = PlatformUtils.createBlockEntityType(LootrInventoryBlockEntity::new, LootrBlockInit.INVENTORY);

    public static void registerBlockEntities() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_chest"), SPECIAL_LOOT_CHEST);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_trapped_loot_chest"), SPECIAL_TRAPPED_LOOT_CHEST);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_shulker"), SPECIAL_LOOT_SHULKER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_barrel"), SPECIAL_LOOT_BARREL);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_inventory"), SPECIAL_LOOT_INVENTORY);
    }
}
