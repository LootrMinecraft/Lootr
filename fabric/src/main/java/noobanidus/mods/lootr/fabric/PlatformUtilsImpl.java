package noobanidus.mods.lootr.fabric;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
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
import noobanidus.mods.lootr.util.functions.*;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> constructor, Block block) {
        return FabricBlockEntityTypeBuilder.create(constructor::apply, block).build(null);
    }

    public static <T extends Entity> EntityType<T> createEntityType(MobCategory category, BiFunction<EntityType<T>, Level, T> factory, EntityDimensions dimensions, int trackRange) {
        return FabricEntityTypeBuilder.create(category, factory::apply)
                .dimensions(dimensions)
                .trackRangeBlocks(trackRange)
                .build();
    }

    public static void registerCommand(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> callback) {
        CommandRegistrationCallback.EVENT.register(callback::consume);
    }

    public static void registerServerStartEvent(Consumer<MinecraftServer> callback) {
        ServerLifecycleEvents.SERVER_STARTING.register(callback::accept);
    }

    public static void registerServerStoppedEvent(Consumer<MinecraftServer> callback) {
        ServerLifecycleEvents.SERVER_STOPPED.register(callback::accept);
    }

    public static void registerServerEndTickEvent(Consumer<MinecraftServer> callback) {
        ServerTickEvents.END_SERVER_TICK.register(callback::accept);
    }

    public static void registerServerChunkLoadEvent(BiConsumer<ServerLevel, LevelChunk> callback) {
        ServerChunkEvents.CHUNK_LOAD.register(callback::accept);
    }

    public static void registerBeforePlayerBlockBreakEvent(PentaFunction<Level, Player, BlockPos, BlockState, BlockEntity, Boolean> callback) {
        PlayerBlockBreakEvents.BEFORE.register(callback::consume);
    }

    public static void registerPlayerBlockBreakCanceledEvent(PentaConsumer<Level, Player, BlockPos, BlockState, BlockEntity> callback) {
        PlayerBlockBreakEvents.CANCELED.register(callback::consume);
    }

    public static void registerClientNetworkReceiver(ResourceLocation location, TetraConsumer<Minecraft, ClientPacketListener, FriendlyByteBuf, ?> callback) {
        ClientPlayNetworking.registerGlobalReceiver(location, (ClientPlayNetworking.PlayChannelHandler)callback);
    }
}
