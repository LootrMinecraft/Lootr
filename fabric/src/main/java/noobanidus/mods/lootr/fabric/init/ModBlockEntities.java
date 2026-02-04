package noobanidus.mods.lootr.fabric.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.block.entity.*;

public class ModBlockEntities {
  public static void registerBlockEntities() {
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.LOOTR_CHEST, LOOTR_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.LOOTR_TRAPPED_CHEST, LOOTR_TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.LOOTR_SHULKER, LOOTR_SHULKER);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.LOOTR_BARREL, LOOTR_BARREL);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.LOOTR_INVENTORY, LOOTR_INVENTORY);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.BRUSHABLE_BLOCK, LOOTR_BRUSHABLE_BLOCK);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.DECORATED_POT, LOOTR_DECORATED_POT);
  }

  public static final BlockEntityType<LootrChestBlockEntity> LOOTR_CHEST = FabricBlockEntityTypeBuilder.create(LootrChestBlockEntity::new, ModBlocks.CHEST)
      .build();
  public static final BlockEntityType<LootrBarrelBlockEntity> LOOTR_BARREL = FabricBlockEntityTypeBuilder.create(LootrBarrelBlockEntity::new, ModBlocks.BARREL)
      .build();
  public static final BlockEntityType<LootrTrappedChestBlockEntity> LOOTR_TRAPPED_CHEST = FabricBlockEntityTypeBuilder.create(LootrTrappedChestBlockEntity::new, ModBlocks.TRAPPED_CHEST)
      .build();
  public static final BlockEntityType<LootrShulkerBlockEntity> LOOTR_SHULKER = FabricBlockEntityTypeBuilder.create(LootrShulkerBlockEntity::new, ModBlocks.SHULKER)
      .build();
  public static final BlockEntityType<LootrInventoryBlockEntity> LOOTR_INVENTORY = FabricBlockEntityTypeBuilder.create(LootrInventoryBlockEntity::new, ModBlocks.INVENTORY)
      .build();
  public static final BlockEntityType<LootrBrushableBlockEntity> LOOTR_BRUSHABLE_BLOCK = FabricBlockEntityTypeBuilder.create(LootrBrushableBlockEntity::new, ModBlocks.SUSPICIOUS_GRAVEL, ModBlocks.SUSPICIOUS_SAND)
      .build();
  public static final BlockEntityType<LootrDecoratedPotBlockEntity> LOOTR_DECORATED_POT = FabricBlockEntityTypeBuilder.create(LootrDecoratedPotBlockEntity::new, ModBlocks.DECORATED_POT)
      .build();
}
