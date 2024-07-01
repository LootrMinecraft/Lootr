package noobanidus.mods.lootr.api.info;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.api.LootrAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface ILootrInfoProvider {
  // TODO: Hm
  default LootrInfoType getInfoType() {
    return LootrInfoType.CUSTOM;
  }

  @NotNull
  default Vec3 getInfoVec() {
    return Vec3.atCenterOf(getInfoPos());
  }

  @Nullable
  default UUID getInfoUUID () {
    return null;
  }

  @NotNull
  BlockPos getInfoPos();

  @NotNull
  ResourceKey<LootTable> getInfoLootTable();

  @Nullable Component getInfoDisplayName ();

  @NotNull
  ResourceKey<Level> getInfoDimension();

  int getInfoContainerSize();

  @Nullable
  default Level getInfoLevel () {
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      return null;
    }

    return server.getLevel(getInfoDimension());
  }

  long getInfoLootSeed();

  @Nullable
  default Optional<? extends RandomizableContainerBlockEntity> asBaseBlockEntity() {
    if (this instanceof RandomizableContainerBlockEntity blockEntity) {
      return Optional.of(blockEntity);
    }
    return Optional.empty();
  }

  @Nullable
  default Optional<? extends AbstractMinecartContainer> asBaseMinecartEntity() {
    if (this instanceof AbstractMinecartContainer minecart) {
      return Optional.of(minecart);
    }
    return Optional.empty();
  }

  static ILootrInfoProvider of(ILootrInfoProvider provider) {
    return provider;
  }

  static ILootrInfoProvider of(RandomizableContainerBlockEntity blockEntity) {
    if (blockEntity instanceof ILootrInfoProvider provider) {
      return provider;
    }
    return new RandomizableContainerBlockEntityLootrInfoProvider(blockEntity);
  }

  static ILootrInfoProvider of(AbstractMinecartContainer minecart) {
    if (minecart instanceof ILootrInfoProvider provider) {
      return provider;
    }
    return new AbstractMinecartContainerLootrInfoProvider(minecart);
  }

  static ILootrInfoProvider of (Supplier<BlockPos> pos, IntSupplier containerSize, Supplier<ResourceKey<LootTable>> lootTable, LongSupplier lootSeed, Supplier<Component> displayName, Supplier<ResourceKey<Level>> dimension) {
    return new CustomLootrInfoProvider(pos, containerSize, lootTable, lootSeed, displayName, dimension);
  }

  enum LootrInfoType {
    RANDOMIZABLE_CONTAINER_BLOCK_ENTITY,
    MINECART_ENTITY,
    CUSTOM
  }
}
