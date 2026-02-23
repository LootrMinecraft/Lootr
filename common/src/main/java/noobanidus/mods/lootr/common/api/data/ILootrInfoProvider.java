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
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.IClientOpeners;
import noobanidus.mods.lootr.common.api.ILootrType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.data.blockentity.RandomizableContainerBlockEntityLootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.entity.AbstractMinecartContainerLootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * This is the commonly used interface for accessing Lootr containers.
 * That said, implementations generally extend ILootrBlockEntity or ILootrEntity,
 * as these provide more functionality.
 * <br />
 * This interface should, however, be used whenever a function interacts
 * with a Lootr container but doesn't need to know about what the actual type is.
 * e.g., LootrAPI::handleProviderOpen.
 */
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

  @Deprecated
  static ILootrInfoProvider of(UUID id, BlockPos pos, int containerSize, ResourceKey<LootTable> lootTable, long lootSeed, Component displayName, ResourceKey<Level> dimension, NonNullList<ItemStack> customInventory, @Deprecated LootrInfoType type, @Deprecated LootrBlockType blockType) {
    // TODO: Guess the new type
    return
        new CustomLootrInfoProvider(id, ILootrInfo.generateInfoKey(id), pos, containerSize, lootTable, lootSeed, displayName, dimension, customInventory, type, blockType, null);
  }

  static ILootrInfoProvider of(UUID id, BlockPos pos, int containerSize, ResourceKey<LootTable> lootTable, long lootSeed, Component displayName, ResourceKey<Level> dimension, NonNullList<ItemStack> customInventory, ILootrType type) {
    return new CustomLootrInfoProvider(id, ILootrInfo.generateInfoKey(id), pos, containerSize, lootTable, lootSeed, displayName, dimension, customInventory, null, null, type);
  }

  // This matters for actual implementations of ILootrBlockEntity
  // but not so much as for ILootrEntity implementations as those
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

  default boolean hasLootAvailable (ServerPlayer player) {
    ILootrInventory inventory = LootrAPI.getInventory(this, player);
    if (inventory == null) {
      return false;
    }

    for (int i = 0; i < inventory.getContainerSize(); i++) {
      if (!inventory.getItem(i).isEmpty()) {
        return true;
      }
    }

    return false;
  }

  @Nullable
  default IContainerTrigger getTrigger() {
    return null;
  }

  default void performTrigger(ServerPlayer player) {
    IContainerTrigger trigger = getTrigger();
    if (trigger != null) {
      if (!hasServerOpened(player)) {
        trigger.trigger(player, getInfoUUID());
      }
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
      NewTickingData.getRefreshData().clearTicking(LootrAPI.getServer(), this.getInfoUUID());
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

  default Vec3 getParticleCenter () {
    BlockPos pos = getInfoPos();
    return new Vec3(pos.getX(), pos.getY(), pos.getZ());
  }

  default double getParticleYOffset () {
    return 0.95;
  }

  default double[] getParticleXBounds() {
    return new double[] {0.25, 0.75};
  }

  default double[] getParticleZBounds() {
    return new double[] {0.25, 0.75};
  }
}
