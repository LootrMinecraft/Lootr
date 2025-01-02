package noobanidus.mods.lootr.common.api.replacement;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.IReplaceableBlockEntityConverter;
import org.jetbrains.annotations.Nullable;

public record RandomizableContainerWrapper(RandomizableContainerBlockEntity blockEntity) implements IReplaceableBlockEntity {
  @Override
  public BlockEntity getBlockEntity() {
    return blockEntity();
  }

  @Override
  public ResourceKey<LootTable> getLootTable() {
    return blockEntity().getLootTable();
  }

  @Override
  public long getSeed() {
    return blockEntity.getLootTableSeed();
  }

  @Override
  public void setLootTable(ResourceKey<LootTable> table) {
    blockEntity().setLootTable(table);
  }

  @Override
  public void setLootSeed(long seed) {
    blockEntity.setLootTableSeed(seed);
  }

  public static class Converter implements IReplaceableBlockEntityConverter {
    @Override
    public @Nullable IReplaceableBlockEntity apply(BlockEntity blockEntity) {
      if (blockEntity instanceof RandomizableContainerBlockEntity randomizable) {
        return new RandomizableContainerWrapper(randomizable);
      }

      return null;
    }

    @Override
    public boolean canConvert(BlockEntity blockEntity) {
      return blockEntity instanceof RandomizableContainerBlockEntity;
    }
  }
}
