package noobanidus.mods.lootr.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.api.ILootrAPI;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.MenuBuilder;
import noobanidus.mods.lootr.api.inventory.ILootrInventory;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.data.SpecialChestInventory;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class LootrAPIImpl implements ILootrAPI {

  @Override
  public boolean isFakePlayer(Player player) {
    if (player instanceof ServerPlayer splayer) {
      return splayer.connection == null || splayer.getClass() != ServerPlayer.class;
    }
    return false;
  }

  @Override
  public boolean clearPlayerLoot(UUID id) {
    return DataStorage.clearInventories(id);
  }

  @Nullable
  @Override
  public ILootrInventory getInventory(Level level, UUID id, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
    return DataStorage.getInventory(level, id, pos, player, blockEntity, filler, tableSupplier, seedSupplier);
  }

  @Nullable
  @Override
  public ILootrInventory getInventory(Level level, UUID id, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier, MenuBuilder menuBuilder) {
    SpecialChestInventory inventory = DataStorage.getInventory(level, id, pos, player, blockEntity, filler, tableSupplier, seedSupplier);
    if (inventory != null) {
      inventory.setMenuBuilder(menuBuilder);
    }
    return inventory;
  }

  @Nullable
  @Override
  public ILootrInventory getInventory(Level level, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
    return DataStorage.getInventory(level, id, pos, player, sizeSupplier, displaySupplier, filler, tableSupplier, seedSupplier);
  }

  @Nullable
  @Override
  public ILootrInventory getInventory(Level level, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier, MenuBuilder menuBuilder) {
    SpecialChestInventory inventory = DataStorage.getInventory(level, id, pos, player, sizeSupplier, displaySupplier, filler, tableSupplier, seedSupplier);
    if (inventory != null) {
      inventory.setMenuBuilder(menuBuilder);
    }
    return inventory;
  }

  @Override
  public long getLootSeed(long seed) {
    if (ConfigManager.get().seed.randomize_seed || seed == -1) {
      return ThreadLocalRandom.current().nextLong();
    }
    return seed;
  }

  @Override
  public boolean isSavingStructure() {
    return shouldDiscard();
  }

  @Override
  public boolean shouldDiscard() {
    return LootrAPI.shouldDiscardIdAndOpeners;
  }

  @Override
  public float getExplosionResistance(Block block, float defaultResistance) {
    if (ConfigManager.get().breaking.blast_resistant) {
      return 16.0f;
    } else if (ConfigManager.get().breaking.blast_immune) {
      return Float.MAX_VALUE;
    } else {
      return defaultResistance;
    }
  }

  @Override
  public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress) {
    if (ConfigManager.get().breaking.disable_break) {
      return 0f;
    }
    return defaultProgress;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal) {
    if (ConfigManager.get().breaking.power_comparators) {
      return 1;
    }
    return defaultSignal;
  }
}
