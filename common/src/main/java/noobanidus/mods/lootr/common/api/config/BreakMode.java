package noobanidus.mods.lootr.common.api.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum BreakMode {
  DEFAULT,
  NEVER,
  ALWAYS;

  public static final IntFunction<BreakMode> BY_ID = ByIdMap.continuous(BreakMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
  public static final StreamCodec<ByteBuf, BreakMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BreakMode::ordinal);
}
