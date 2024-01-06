package noobanidus.mods.lootr.util;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.injectables.annotations.ExpectPlatform;
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
import org.jetbrains.annotations.Contract;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class PlatformUtils {
    private PlatformUtils() {}

    @Contract
    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> constructor, Block block) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static <T extends Entity> EntityType<T> createEntityType(MobCategory category, BiFunction<EntityType<T>, Level, T> factory, EntityDimensions dimensions, int trackRange) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static void registerCommand(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> callback) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static void registerServerStartEvent(Consumer<MinecraftServer> callback) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static void registerServerStoppedEvent(Consumer<MinecraftServer> callback) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static void registerServerEndTickEvent(Consumer<MinecraftServer> callback) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static void registerServerChunkLoadEvent(BiConsumer<ServerLevel, LevelChunk> callback) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static void registerBeforePlayerBlockBreakEvent(PentaFunction<Level, Player, BlockPos, BlockState, BlockEntity, Boolean> callback) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static void registerPlayerBlockBreakCanceledEvent(PentaConsumer<Level, Player, BlockPos, BlockState, BlockEntity> callback) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static void registerClientNetworkReceiver(ResourceLocation location, TetraConsumer<Minecraft, ClientPacketListener, FriendlyByteBuf, ?> callback) {
        throw new UnsupportedOperationException();
    }
}
