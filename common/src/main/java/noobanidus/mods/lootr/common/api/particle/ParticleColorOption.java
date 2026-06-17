package noobanidus.mods.lootr.common.api.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;

import java.util.function.Function;

public record ParticleColorOption(ParticleType<?> type, int color1, int color2,
                                  boolean rainbow) implements ParticleOptions {
  private static final Function<ParticleType<ParticleColorOption>, MapCodec<ParticleColorOption>> CODEC = Util.memoize(ParticleColorOption::codec);

  private static MapCodec<ParticleColorOption> codec(ParticleType<ParticleColorOption> type) {
    return RecordCodecBuilder.mapCodec(instance ->
        instance.group(Codec.INT.fieldOf("color1").forGetter(ParticleColorOption::color1), Codec.INT.fieldOf("color2")
                .forGetter(ParticleColorOption::color2), Codec.BOOL.fieldOf("rainbow")
                .forGetter(ParticleColorOption::rainbow))
            .apply(instance, (c1, c2, r) -> new ParticleColorOption(type, c1, c2, r))
    );
  }

  private static final Function<ParticleType<ParticleColorOption>, StreamCodec<ByteBuf, ParticleColorOption>> STREAM_CODEC = Util.memoize(ParticleColorOption::streamCodec);

  public static StreamCodec<ByteBuf, ParticleColorOption> streamCodec(ParticleType<ParticleColorOption> type) {
    return StreamCodec.composite(ByteBufCodecs.VAR_INT, ParticleColorOption::color1, ByteBufCodecs.VAR_INT, ParticleColorOption::color2, ByteBufCodecs.BOOL, ParticleColorOption::rainbow, (c1, c2, r) -> new ParticleColorOption(type, c1, c2, r));
  }

  @Override
  public ParticleType<?> getType() {
    return this.type;
  }

  public static ParticleType<ParticleColorOption> create(boolean overrideLimiter) {
    return new ParticleType<>(overrideLimiter) {
      @Override
      public MapCodec<ParticleColorOption> codec() {
        return CODEC.apply(this);
      }

      @Override
      public StreamCodec<? super RegistryFriendlyByteBuf, ParticleColorOption> streamCodec() {
        return STREAM_CODEC.apply(this);
      }
    };
  }
}
