package noobanidus.mods.lootr.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import noobanidus.mods.lootr.api.ILootrHooks;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.data.DataStorage;

import java.util.UUID;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class LootrHooksImpl implements ILootrHooks {
  @Override
  public boolean clearPlayerLoot(UUID id) {
    return DataStorage.clearInventories(id);
  }

  @Override
  public MenuProvider getModdedMenu(Level level, UUID id, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
    return DataStorage.getInventory(level, id, pos, player, blockEntity, filler, tableSupplier, seedSupplier);
  }
}
