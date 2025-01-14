package noobanidus.mods.lootr.common.api.replacement;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.IReplaceableBlockEntityConverter;
import noobanidus.mods.lootr.common.mixins.AccessorMixinBrushableBlockEntity;
import org.jetbrains.annotations.Nullable;

public record BrushableWrapper (BrushableBlockEntity blockEntity) implements IReplaceableBlockEntity {
  @Override
  public BrushableBlockEntity getBlockEntity() {
    return blockEntity;
  }

  @Override
  public ResourceKey<LootTable> getLootTable() {
    return ((AccessorMixinBrushableBlockEntity) blockEntity()).getLootTable();
  }

  @Override
  public long getSeed() {
    return ((AccessorMixinBrushableBlockEntity) blockEntity()).getLootTableSeed();
  }

  @Override
  public void setLootTable(ResourceKey<LootTable> table) {
    blockEntity().setLootTable(table, -1);
  }

  @Override
  public void setLootSeed(long seed) {
    blockEntity().setLootTable(getLootTable(), seed);
  }

  @Override
  public void setLootTable(ResourceKey<LootTable> table, long seed) {
    blockEntity().setLootTable(table, seed);
  }

  public static class Converter implements IReplaceableBlockEntityConverter {

    @Override
    public @Nullable IReplaceableBlockEntity apply(BlockEntity blockEntity) {
      if (blockEntity instanceof BrushableBlockEntity brushable) {
        return new BrushableWrapper(brushable);
      }

      return null;
    }

    @Override
    public boolean canConvert(BlockEntity blockEntity) {
      return blockEntity instanceof BrushableBlockEntity;
    }

    @Override
    public @Nullable BlockEntityType<?> getBlockEntityType() {
      return BlockEntityType.BRUSHABLE_BLOCK;
    }
  }
}
