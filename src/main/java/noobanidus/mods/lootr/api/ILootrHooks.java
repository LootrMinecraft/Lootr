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

@Deprecated
public interface ILootrHooks {
  default boolean clearPlayerLoot(ServerPlayer entity) {
    return clearPlayerLoot(entity.getUUID());
  }

  boolean clearPlayerLoot(UUID id);

  MenuProvider getModdedMenu(Level level, UUID id, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier);
}
