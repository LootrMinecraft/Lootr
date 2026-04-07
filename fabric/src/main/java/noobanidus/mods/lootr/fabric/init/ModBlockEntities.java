package noobanidus.mods.lootr.fabric.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.block.entity.*;

public class ModBlockEntities {
  public static void registerBlockEntities() {
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.CHEST, CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.SHULKER_BOX, SHULKER_BOX);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.BARREL, BARREL);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.BRUSHABLE_BLOCK, BRUSHABLE_BLOCK);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.DECORATED_POT, DECORATED_POT);
  }

  public static final BlockEntityType<LootrChestBlockEntity> CHEST = FabricBlockEntityTypeBuilder.create(LootrChestBlockEntity::new, ModBlocks.CHEST)
      .build();
  public static final BlockEntityType<LootrBarrelBlockEntity> BARREL = FabricBlockEntityTypeBuilder.create(LootrBarrelBlockEntity::new, ModBlocks.BARREL)
      .build();
  public static final BlockEntityType<LootrTrappedChestBlockEntity> TRAPPED_CHEST = FabricBlockEntityTypeBuilder.create(LootrTrappedChestBlockEntity::new, ModBlocks.TRAPPED_CHEST)
      .build();
  public static final BlockEntityType<LootrShulkerBoxBlockEntity> SHULKER_BOX = FabricBlockEntityTypeBuilder.create(LootrShulkerBoxBlockEntity::new, ModBlocks.SHULKER_BOX)
      .build();
  public static final BlockEntityType<LootrBrushableBlockEntity> BRUSHABLE_BLOCK = FabricBlockEntityTypeBuilder.create(LootrBrushableBlockEntity::new, ModBlocks.SUSPICIOUS_GRAVEL, ModBlocks.SUSPICIOUS_SAND)
      .build();
  public static final BlockEntityType<LootrDecoratedPotBlockEntity> DECORATED_POT = FabricBlockEntityTypeBuilder.create(LootrDecoratedPotBlockEntity::new, ModBlocks.DECORATED_POT)
      .build();
}
