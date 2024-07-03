package noobanidus.mods.lootr.api;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.util.ChestUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface ILootInfoProvider {
  default LootInfoType getInfoType() {
    return LootInfoType.CUSTOM;
  }

  default Vec3 getInfoVec() {
    return Vec3.atCenterOf(getInfoPos());
  }

  @Nullable
  default UUID getInfoUUID() {
    return null;
  }

  BlockPos getInfoPos();

  ResourceKey<LootTable> getInfoLootTable();

  long getInfoLootSeed();

  Level getInfoLevel();

  @Nullable
  default Optional<? extends RandomizableContainerBlockEntity> asBlockEntity() {
    return Optional.empty();
  }

  @Nullable
  default Optional<? extends AbstractMinecartContainer> asMinecart() {
    return Optional.empty();
  }

  default void unpackLootTable(ILootInfoProvider provider, Player player, Container inventory, @Nullable ResourceKey<LootTable> overrideTable, long overrideSeed) {
    Level level = provider.getInfoLevel();
    BlockPos pos = provider.getInfoPos();
    ResourceKey<LootTable> lootTable = overrideTable == null ? provider.getInfoLootTable() : overrideTable;
    long seed = overrideSeed == Long.MIN_VALUE ? provider.getInfoLootSeed() : overrideSeed;
    if (lootTable != null) {
      LootTable loottable = level.getServer().reloadableRegistries().getLootTable(lootTable);

      if (loottable == LootTable.EMPTY) {
        LootrAPI.LOG.error("Unable to fill loot barrel in " + level.dimension().location() + " at " + pos + " as the loot table '" + lootTable.location() + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
        if (ConfigManager.get().debug.report_unresolved_tables) {
          player.displayClientMessage(ChestUtil.getInvalidTable(lootTable), false);
        }
      }

      if (player instanceof ServerPlayer sPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger(sPlayer, lootTable);
      }

      LootParams.Builder builder = new LootParams.Builder((ServerLevel) level).withParameter(LootContextParams.ORIGIN, provider.getInfoVec());
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
      }

      loottable.fill(inventory, builder.create(LootContextParamSets.CHEST), LootrAPI.getLootSeed(seed));
    }
  }

  static ILootInfoProvider of(ILootInfoProvider provider) {
    return provider;
  }

  static ILootInfoProvider of(RandomizableContainerBlockEntity blockEntity) {
    if (blockEntity instanceof ILootInfoProvider provider) {
      return provider;
    }
    return new RandomizableContainerBlockEntityLootInfoProvider(blockEntity);
  }

  static ILootInfoProvider of(AbstractMinecartContainer minecart) {
    if (minecart instanceof ILootInfoProvider provider) {
      return provider;
    }
    return new AbstractMinecartContainerLootInfoProvider(minecart);
  }

  static ILootInfoProvider of(Supplier<BlockPos> pos, Supplier<ResourceKey<LootTable>> lootTable, LongSupplier lootSeed, Level level) {
    return new CustomLootInfoProvider(pos, lootTable, lootSeed, level);
  }

  record CustomLootInfoProvider(
      Supplier<BlockPos> pos,
      Supplier<ResourceKey<LootTable>> lootTable,
      LongSupplier lootSeed,
      Level level) implements ILootInfoProvider {

    @Override
    public BlockPos getInfoPos() {
      return pos.get();
    }

    @Override
    public ResourceKey<LootTable> getInfoLootTable() {
      return lootTable.get();
    }

    @Override
    public long getInfoLootSeed() {
      return lootSeed.getAsLong();
    }

    @Override
    public Level getInfoLevel() {
      return level;
    }
  }

  record AbstractMinecartContainerLootInfoProvider(
      AbstractMinecartContainer minecart) implements ILootInfoProvider {

    @Override
    public LootInfoType getInfoType() {
      return LootInfoType.MINECART_ENTITY;
    }

    @Override
    public Vec3 getInfoVec() {
      return minecart.position();
    }

    @Override
    public BlockPos getInfoPos() {
      return minecart.blockPosition();
    }

    @Override
    public ResourceKey<LootTable> getInfoLootTable() {
      return minecart.getLootTable();
    }

    @Override
    public long getInfoLootSeed() {
      return minecart.getLootTableSeed();
    }

    @Override
    public Level getInfoLevel() {
      return minecart.level();
    }

    @Override
    public Optional<AbstractMinecartContainer> asMinecart() {
      return Optional.of(minecart);
    }
  }

  record RandomizableContainerBlockEntityLootInfoProvider(
      RandomizableContainerBlockEntity blockEntity) implements ILootInfoProvider {

    @Override
    public LootInfoType getInfoType() {
      return LootInfoType.RANDOMIZABLE_CONTAINER_BLOCK_ENTITY;
    }

    @Override
    public BlockPos getInfoPos() {
      return blockEntity.getBlockPos();
    }

    @Override
    public ResourceKey<LootTable> getInfoLootTable() {
      return blockEntity.getLootTable();
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
    public Optional<RandomizableContainerBlockEntity> asBlockEntity() {
      return Optional.of(blockEntity);
    }
  }

  enum LootInfoType {
    RANDOMIZABLE_CONTAINER_BLOCK_ENTITY,
    MINECART_ENTITY,
    CUSTOM
  }
}