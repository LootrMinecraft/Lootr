package noobanidus.mods.lootr.api;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

import java.util.UUID;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class LootrHooks implements ILootrHooks {
  public static ILootrHooks INSTANCE;

  @Override
  public boolean clearPlayerLoot(ServerPlayer entity) {
    return INSTANCE.clearPlayerLoot(entity.getUUID());
  }

  @Override
  public boolean clearPlayerLoot(UUID id) {
    return INSTANCE.clearPlayerLoot(id);
  }

  @Override
  public MenuProvider getModdedMenu(Level level, UUID id, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
    return INSTANCE.getModdedMenu(level, id, pos, player, blockEntity, filler, tableSupplier, seedSupplier);
  }
}
