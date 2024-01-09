package noobanidus.mods.lootr.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class LootrAPI {
    public static final Logger LOG = LogManager.getLogger();
    public static final String MODID = "lootr";

    public static ILootrAPI INSTANCE;

    public static boolean isFakePlayer(Player entity) {
        return INSTANCE.isFakePlayer(entity);
    }

    public static boolean clearPlayerLoot(ServerPlayer entity) {
        return INSTANCE.clearPlayerLoot(entity);
    }

    public static boolean clearPlayerLoot(UUID id) {
        return INSTANCE.clearPlayerLoot(id);
    }

    public static MenuProvider getModdedMenu(Level level, UUID id, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
        return INSTANCE.getModdedMenu(level, id, pos, player, blockEntity, filler, tableSupplier, seedSupplier);
    }

    public static MenuProvider getModdedMenu(Level level, UUID id, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier, MenuBuilder builder) {
        return INSTANCE.getModdedMenu(level, id, pos, player, blockEntity, filler, tableSupplier, seedSupplier, builder);
    }

    public static MenuProvider getModdedMenu(Level level, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
        return INSTANCE.getModdedMenu(level, id, pos, player, sizeSupplier, displaySupplier, filler, tableSupplier, seedSupplier);
    }

    public static MenuProvider getModdedMenu(Level level, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier, MenuBuilder builder) {
        return INSTANCE.getModdedMenu(level, id, pos, player, sizeSupplier, displaySupplier, filler, tableSupplier, seedSupplier, builder);
    }

    public static long getLootSeed(long seed) {
        return INSTANCE.getLootSeed(seed);
    }
}
