package noobanidus.mods.lootr.api.info;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record RandomizableContainerBlockEntityLootrInfoProvider(
    @NotNull RandomizableContainerBlockEntity blockEntity) implements ILootrInfoProvider {

  @Override
  public LootrInfoType getInfoType() {
    return LootrInfoType.RANDOMIZABLE_CONTAINER_BLOCK_ENTITY;
  }

  @Override
  public @NotNull BlockPos getInfoPos() {
    return blockEntity.getBlockPos();
  }

  // TODO: Can return null
  @Override
  public @NotNull ResourceKey<LootTable> getInfoLootTable() {
    return blockEntity.getLootTable();
  }

  @Override
  public @Nullable Component getInfoDisplayName() {
    return blockEntity.getDisplayName();
  }

  @Override
  public @NotNull ResourceKey<Level> getInfoDimension() {
    return blockEntity.getLevel().dimension();
  }

  @Override
  public int getInfoContainerSize() {
    return blockEntity.getContainerSize();
  }

  @Override
  public long getInfoLootSeed() {
    return blockEntity.getLootTableSeed();
  }

  @Override
  public Level getInfoLevel() {
    return blockEntity.getLevel();
  }

  @Override
  public Optional<RandomizableContainerBlockEntity> asBaseBlockEntity() {
    return Optional.of(blockEntity);
  }
}
