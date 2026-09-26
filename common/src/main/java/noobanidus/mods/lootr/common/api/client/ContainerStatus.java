package noobanidus.mods.lootr.common.api.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.Locale;
import java.util.function.IntFunction;

public enum ContainerStatus {
  REFRESH,
  DECAY;

  public static final IntFunction<ContainerStatus> BY_ID = ByIdMap.continuous(ContainerStatus::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
  public static final StreamCodec<ByteBuf, ContainerStatus> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ContainerStatus::ordinal);

  public enum Type {
    START,
    ONGOING,
    COMPLETE;

    public static final IntFunction<Type> BY_ID = ByIdMap.continuous(Type::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Type::ordinal);
  }

  public record Message(Component title, Component message) {

  }

  public static Message getMessage(ContainerStatus status, Type type, int remainingValue) {
    String start = "lootr.toast." + status.name().toLowerCase(Locale.ROOT) + ".";
    Component title = Component.translatable(start + type.name().toLowerCase(Locale.ROOT));
    Component message;
    if (type == Type.COMPLETE) {
      if (status == DECAY) {
        message = Component.empty();
      } else {
        message = Component.translatable(start + "complete_message");
      }
    } else {
      message = Component.translatable(start + "message", remainingValue);
    }
    return new Message(title, message);
  }
}
