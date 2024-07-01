package noobanidus.mods.lootr.api.info;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public record CustomLootrInfoProvider(
    Supplier<BlockPos> pos,
    IntSupplier containerSize,
    Supplier<ResourceKey<LootTable>> lootTable,
    LongSupplier lootSeed,
    Supplier<Component> displayName,
    Supplier<ResourceKey<Level>> dimension) implements ILootrInfoProvider {

  @Override
  public BlockPos getInfoPos() {
    return pos.get();
  }

  @Override
  public ResourceKey<LootTable> getInfoLootTable() {
    return lootTable.get();
  }

  @Override
  public @Nullable Component getInfoDisplayName() {
    return displayName.get();
  }

  @Override
  public @NotNull ResourceKey<Level> getInfoDimension() {
    return dimension.get();
  }

  @Override
  public int getInfoContainerSize() {
    return containerSize.getAsInt();
  }

  @Override
  public long getInfoLootSeed() {
    return lootSeed.getAsLong();
  }
}
