package noobanidus.mods.lootr.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.block.entities.*;
import noobanidus.mods.lootr.config.LootrModConfig;

public class LootrBlockEntityInit {
  public static final BlockEntityType<LootrChestBlockEntity> SPECIAL_LOOT_CHEST = FabricBlockEntityTypeBuilder.create(LootrChestBlockEntity::new, LootrBlockInit.CHEST).build(null);
  public static final BlockEntityType<LootrBarrelBlockEntity> SPECIAL_LOOT_BARREL = FabricBlockEntityTypeBuilder.create(LootrBarrelBlockEntity::new, LootrBlockInit.BARREL).build(null);
  public static final BlockEntityType<LootrTrappedChestBlockEntity> SPECIAL_TRAPPED_LOOT_CHEST = FabricBlockEntityTypeBuilder.create(LootrTrappedChestBlockEntity::new, LootrBlockInit.TRAPPED_CHEST).build(null);
  public static final BlockEntityType<LootrShulkerBlockEntity> SPECIAL_LOOT_SHULKER = FabricBlockEntityTypeBuilder.create(LootrShulkerBlockEntity::new, LootrBlockInit.SHULKER).build(null);
  public static final BlockEntityType<LootrInventoryBlockEntity> SPECIAL_LOOT_INVENTORY = FabricBlockEntityTypeBuilder.create(LootrInventoryBlockEntity::new, LootrBlockInit.INVENTORY).build(null);

  public static void registerBlockEntities() {
    Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_chest"), SPECIAL_LOOT_CHEST);
    Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_trapped_loot_chest"), SPECIAL_TRAPPED_LOOT_CHEST);
    Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_shulker"), SPECIAL_LOOT_SHULKER);
    Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_barrel"), SPECIAL_LOOT_BARREL);
    Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_inventory"), SPECIAL_LOOT_INVENTORY);
  }
}
