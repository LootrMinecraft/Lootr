package noobanidus.mods.lootr.common.api.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.IClientOpeners;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.data.blockentity.RandomizableContainerBlockEntityLootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.entity.AbstractMinecartContainerLootrInfoProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface ILootrInfoProvider extends ILootrInfo, IClientOpeners {
  static ILootrInfoProvider of(BlockPos pos, Level level) {
    if (level.isClientSide()) {
      return null;
    }
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrInfoProvider provider) {
      return provider;
    } else if (blockEntity instanceof ILootrInfoProvider provider) {
      return provider;
    }
    return null;
  }

  static ILootrInfoProvider of(RandomizableContainerBlockEntity blockEntity, UUID id) {
    if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrInfoProvider provider) {
      return provider;
    } else if (blockEntity instanceof ILootrInfoProvider provider) {
      return provider;
    }
    return new RandomizableContainerBlockEntityLootrInfoProvider(blockEntity, id, ILootrInfo.generateInfoKey(id), null);
  }

  static ILootrInfoProvider of(RandomizableContainerBlockEntity blockEntity, UUID id, NonNullList<ItemStack> customInventory) {
    if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrInfoProvider provider) {
      return provider;
    } else if (blockEntity instanceof ILootrInfoProvider provider) {
      return provider;
    }
    return new RandomizableContainerBlockEntityLootrInfoProvider(blockEntity, id, ILootrInfo.generateInfoKey(id), customInventory);
  }

  static ILootrInfoProvider of(AbstractMinecartContainer minecart) {
    if (minecart instanceof ILootrInfoProvider provider) {
      return provider;
    }
    return new AbstractMinecartContainerLootrInfoProvider(minecart, ILootrInfo.generateInfoKey(minecart.getUUID()));
  }

  static ILootrInfoProvider of(UUID id, BlockPos pos, int containerSize, ResourceKey<LootTable> lootTable, long lootSeed, Component displayName, ResourceKey<Level> dimension, NonNullList<ItemStack> customInventory, LootrInfoType type, LootrBlockType blockType) {
    return new CustomLootrInfoProvider(id, ILootrInfo.generateInfoKey(id), pos, containerSize, lootTable, lootSeed, displayName, dimension, customInventory, type, blockType);
  }

  // This matters for actual implementations of ILootrBlockEntity
  // but not so much as for ILootrCart implementations as those
  // do not actually track openers; there's no reason why they
  // couldn't though...
  default int getPhysicalOpenerCount () {
    return -1;
  }

  @Override
  default Set<UUID> getVisualOpeners() {
    ILootrSavedData data = LootrAPI.getData(this);
    if (data != null) {
      return data.getVisualOpeners();
    }
    return null;
  }

  @Override
  default Set<UUID> getActualOpeners() {
    ILootrSavedData data = LootrAPI.getData(this);
    if (data != null) {
      return data.getActualOpeners();
    }
    return null;
  }

  @Override
  default boolean hasBeenOpened() {
    ILootrSavedData data = LootrAPI.getData(this);
    if (data == null) {
      return false;
    }

    return data.hasBeenOpened();
  }

  @Nullable
  default IContainerTrigger getTrigger() {
    return null;
  }

  default void performTrigger(ServerPlayer player) {
    IContainerTrigger trigger = getTrigger();
    if (trigger != null) {
      trigger.trigger(player, getInfoUUID());
    }
  }

  default void performOpen(ServerPlayer player) {
  }

  default void performOpen() {
  }

  default void performClose(ServerPlayer player) {
  }

  default void performClose() {
  }

  default void performDecay() {
  }

  default void performRefresh() {
    ILootrSavedData data = LootrAPI.getData(this);
    if (data != null) {
      data.refresh();
      data.clearOpeners();
      markChanged();
    }
  }

  default void performUpdate(ServerPlayer player) {
  }

  default void performUpdate() {
  }

  @Override
  default void markDataChanged() {
    ILootrSavedData data = LootrAPI.getData(this);
    if (data != null) {
      data.markChanged();
    }
  }
}
