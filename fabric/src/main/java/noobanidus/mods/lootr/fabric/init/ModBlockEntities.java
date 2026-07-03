package noobanidus.mods.lootr.fabric.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.*;

public class ModBlockEntities {
  public static void registerBlockEntities() {
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.Identifiers.CHEST, CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.Identifiers.TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.Identifiers.SHULKER_BOX, SHULKER_BOX);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.Identifiers.BARREL, BARREL);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.Identifiers.BRUSHABLE_BLOCK, BRUSHABLE_BLOCK);
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, LootrConstants.Identifiers.DECORATED_POT, DECORATED_POT);
  }

  public static final BlockEntityType<LootrChestBlockEntity> CHEST = FabricBlockEntityTypeBuilder.create(LootrChestBlockEntity::new, LootrRegistry.getChestBlock(), LootrRegistry.getCopperChestBlock(), LootrRegistry.getWeatheredCopperChestBlock(), LootrRegistry.getExposedCopperChestBlock(), LootrRegistry.getOxidizedCopperChestBlock())
      .build();
  public static final BlockEntityType<LootrBarrelBlockEntity> BARREL = FabricBlockEntityTypeBuilder.create(LootrBarrelBlockEntity::new, LootrRegistry.getBarrelBlock())
      .build();
  public static final BlockEntityType<LootrTrappedChestBlockEntity> TRAPPED_CHEST = FabricBlockEntityTypeBuilder.create(LootrTrappedChestBlockEntity::new, LootrRegistry.getTrappedChestBlock())
      .build();
  public static final BlockEntityType<LootrShulkerBoxBlockEntity> SHULKER_BOX = FabricBlockEntityTypeBuilder.create(LootrShulkerBoxBlockEntity::new, LootrRegistry.getShulkerBoxBlock())
      .build();
  public static final BlockEntityType<LootrBrushableBlockEntity> BRUSHABLE_BLOCK = FabricBlockEntityTypeBuilder.create(LootrBrushableBlockEntity::new, LootrRegistry.getSuspiciousGravelBlock(), LootrRegistry.getSuspiciousSandBlock())
      .build();
  public static final BlockEntityType<LootrDecoratedPotBlockEntity> DECORATED_POT = FabricBlockEntityTypeBuilder.create(LootrDecoratedPotBlockEntity::new, LootrRegistry.getDecoratedPotBlock())
      .build();
}
