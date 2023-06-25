package net.zestyblaze.lootr.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface ILootrAPI {
    boolean isFakePlayer (Player player);

    default boolean clearPlayerLoot(ServerPlayer entity) {
        return clearPlayerLoot(entity.getUUID());
    }

    boolean clearPlayerLoot(UUID id);

    @Nullable MenuProvider getModdedMenu(Level world, UUID id, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier);

     @Nullable MenuProvider getModdedMenu(Level world, UUID id, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier, MenuBuilder builder);

    @Nullable MenuProvider getModdedMenu(Level world, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier);

    @Nullable MenuProvider getModdedMenu(Level world, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier, MenuBuilder builder);

    /**
     * Provides access to the relevant configuration for the loot seed. This is used to determine if the provided seed is randomized or not.
     */
    long getLootSeed(long seed);
}
