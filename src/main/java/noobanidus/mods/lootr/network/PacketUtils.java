package noobanidus.mods.lootr.network;

import com.mojang.datafixers.util.Function9;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class PacketUtils {

    private PacketUtils() {
    }

    //Similar to NeoForgeStreamCodecs#enumCodec but allows for keeping it as a ByteBuf and wrapping the value
    public static <V extends Enum<V>> StreamCodec<ByteBuf, V> enumCodec(Class<V> enumClass) {
        return ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, enumClass.getEnumConstants(), ByIdMap.OutOfBoundsStrategy.WRAP), Enum::ordinal);
    }

    public static void log(String logFormat, Object... params) {
    }

    public static <OBJ> OBJ read(RegistryAccess registryAccess, byte[] rawData, Function<RegistryFriendlyByteBuf, OBJ> deserializer) {
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(rawData), registryAccess);
        try {
            return deserializer.apply(buffer);
        } finally {
            buffer.release();
        }
    }

    /**
     * Send this message to the server.
     *
     * @param message - the message to send
     */
    public static <MSG extends CustomPacketPayload> boolean sendToServer(MSG message) {
        PacketDistributor.sendToServer(message);
        return true;
    }

    public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, BlockEntity tile) {
        sendToAllTracking(message, tile.getLevel(), tile.getBlockPos());
    }

    public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, Level world, BlockPos pos) {
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) world, new ChunkPos(pos), message);
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, C> composite(
          final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1,
          final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2,
          final StreamCodec<? super B, T3> codec3, final Function<C, T3> getter3,
          final StreamCodec<? super B, T4> codec4, final Function<C, T4> getter4,
          final StreamCodec<? super B, T5> codec5, final Function<C, T5> getter5,
          final StreamCodec<? super B, T6> codec6, final Function<C, T6> getter6,
          final StreamCodec<? super B, T7> codec7, final Function<C, T7> getter7,
          final StreamCodec<? super B, T8> codec8, final Function<C, T8> getter8,
          final StreamCodec<? super B, T9> codec9, final Function<C, T9> getter9,
          final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> factory) {
        return new StreamCodec<>() {
            @NotNull
            @Override
            public C decode(@NotNull B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                T8 t8 = codec8.decode(buffer);
                T9 t9 = codec9.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9);
            }

            @Override
            public void encode(@NotNull B buffer, @NotNull C obj) {
                codec1.encode(buffer, getter1.apply(obj));
                codec2.encode(buffer, getter2.apply(obj));
                codec3.encode(buffer, getter3.apply(obj));
                codec4.encode(buffer, getter4.apply(obj));
                codec5.encode(buffer, getter5.apply(obj));
                codec6.encode(buffer, getter6.apply(obj));
                codec7.encode(buffer, getter7.apply(obj));
                codec8.encode(buffer, getter8.apply(obj));
                codec9.encode(buffer, getter9.apply(obj));
            }
        };
    }
}
