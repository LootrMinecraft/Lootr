package noobanidus.mods.lootr.util.forge;

import com.google.gson.internal.LinkedTreeMap;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import noobanidus.mods.lootr.util.functions.PentaConsumer;
import noobanidus.mods.lootr.util.functions.PentaFunction;
import noobanidus.mods.lootr.util.functions.TetraConsumer;
import noobanidus.mods.lootr.util.functions.TriConsumer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
    public static final Collection<TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection>> COMMAND_ENTRIES;
    public static final Collection<Consumer<MinecraftServer>> SERVER_START_HANDLERS;
    public static final Collection<Consumer<MinecraftServer>> SERVER_STOP_HANDLERS;
    public static final Collection<Consumer<MinecraftServer>> SERVER_END_TICK_HANDLERS;
    public static final Collection<BiConsumer<ServerLevel, LevelChunk>> CHUNK_LOAD_HANDLERS;
    public static final Collection<PentaFunction<Level, Player, BlockPos, BlockState, BlockEntity, Boolean>> PLAYER_BREAK_BLOCK_HANDLERS;
    public static final Collection<PentaConsumer<Level, Player, BlockPos, BlockState, BlockEntity>> PLAYER_BREAK_BLOCK_CANCELED_HANDLERS;
    public static final Map<ResourceLocation, Collection<TetraConsumer<Minecraft, ClientPacketListener, FriendlyByteBuf, Object>>> CLIENT_CUSTOM_NETWORK_HANDLERS;

    static {
        COMMAND_ENTRIES = new LinkedHashSet<>();
        SERVER_START_HANDLERS = new LinkedHashSet<>();
        SERVER_STOP_HANDLERS = new LinkedHashSet<>();
        SERVER_END_TICK_HANDLERS = new LinkedHashSet<>();
        CHUNK_LOAD_HANDLERS = new LinkedHashSet<>();
        PLAYER_BREAK_BLOCK_HANDLERS = new LinkedHashSet<>();
        PLAYER_BREAK_BLOCK_CANCELED_HANDLERS = new LinkedHashSet<>();
        CLIENT_CUSTOM_NETWORK_HANDLERS = new LinkedTreeMap<>();
    }

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> constructor, Block block) {
        return BlockEntityType.Builder.of(constructor::apply, block).build(null);
    }

    public static <T extends Entity> EntityType<T> createEntityType(MobCategory category, BiFunction<EntityType<T>, Level, T> factory, EntityDimensions dimensions, int trackRange, String name) {
        return EntityType.Builder.of(factory::apply, category).sized(dimensions.width, dimensions.height).setTrackingRange(trackRange).build(name);
    }

    public static void registerCommand(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> callback) {
        COMMAND_ENTRIES.add(callback);
    }

    public static void registerServerStartEvent(Consumer<MinecraftServer> callback) {
        SERVER_START_HANDLERS.add(callback);
    }

    public static void registerServerStoppedEvent(Consumer<MinecraftServer> callback) {
        SERVER_STOP_HANDLERS.add(callback);
    }

    public static void registerServerEndTickEvent(Consumer<MinecraftServer> callback) {
        SERVER_END_TICK_HANDLERS.add(callback);
    }

    public static void registerServerChunkLoadEvent(BiConsumer<ServerLevel, LevelChunk> callback) {
        CHUNK_LOAD_HANDLERS.add(callback);
    }

    public static void registerBeforePlayerBlockBreakEvent(PentaFunction<Level, Player, BlockPos, BlockState, BlockEntity, Boolean> callback) {
        PLAYER_BREAK_BLOCK_HANDLERS.add(callback);
    }

    public static void registerPlayerBlockBreakCanceledEvent(PentaConsumer<Level, Player, BlockPos, BlockState, BlockEntity> callback) {
        PLAYER_BREAK_BLOCK_CANCELED_HANDLERS.add(callback);
    }

    public static void registerClientNetworkReceiver(ResourceLocation location, TetraConsumer<Minecraft, ClientPacketListener, FriendlyByteBuf, Object> callback) {
        CLIENT_CUSTOM_NETWORK_HANDLERS.computeIfAbsent(location, l -> new LinkedHashSet<>()).add(callback);
    }
}
