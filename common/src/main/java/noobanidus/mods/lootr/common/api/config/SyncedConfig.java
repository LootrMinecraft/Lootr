package noobanidus.mods.lootr.common.api.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.function.Function;

public record SyncedConfig(BreakMode break_mode,
                           boolean team_loot, Identifier pinned_team_resolver) {
  public static final StreamCodec<ByteBuf, SyncedConfig> STREAM_CODEC = StreamCodec.composite(
      BreakMode.STREAM_CODEC, SyncedConfig::break_mode,
      ByteBufCodecs.BOOL, SyncedConfig::team_loot,
      Identifier.STREAM_CODEC, SyncedConfig::pinned_team_resolver,
      SyncedConfig::new
  );

  private static <T> T helper(T currentValue, Function<SyncedConfig, T> supplier) {
    var config = LootrAPI.getSyncedConfig();

    if (config == null) {
      return currentValue;
    }

    return supplier.apply(config);
  }

  public static BreakMode getBreakMode(BreakMode currentValue) {
    return helper(currentValue, SyncedConfig::break_mode);
  }

  public static boolean getTeamLootEnabled(boolean currentValue) {
    return helper(currentValue, SyncedConfig::team_loot);
  }

  public static Identifier getPinnedResolver(Identifier currentValue) {
    return helper(currentValue, SyncedConfig::pinned_team_resolver);
  }
}
