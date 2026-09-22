package noobanidus.mods.lootr.common.api.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.function.Function;

public record SyncedConfig(boolean bypass_spawn_protection, boolean disable_break, boolean enable_break,
                           boolean team_loot, ResourceLocation pinned_team_resolver) {
  public static final StreamCodec<ByteBuf, SyncedConfig> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, SyncedConfig::bypass_spawn_protection,
      ByteBufCodecs.BOOL, SyncedConfig::disable_break,
      ByteBufCodecs.BOOL, SyncedConfig::enable_break,
      ByteBufCodecs.BOOL, SyncedConfig::team_loot,
      ResourceLocation.STREAM_CODEC, SyncedConfig::pinned_team_resolver,
      SyncedConfig::new
  );

  private static <T> T helper(T currentValue, Function<SyncedConfig, T> supplier) {
    var config = LootrAPI.getSyncedConfig();

    if (config == null) {
      return currentValue;
    }

    return supplier.apply(config);
  }

  public static boolean getBypassSpawnProtection(boolean currentValue) {
    return helper(currentValue, SyncedConfig::bypass_spawn_protection);
  }

  public static boolean getDisableBreak(boolean currentValue) {
    return helper(currentValue, SyncedConfig::disable_break);
  }

  public static boolean getEnableBreak(boolean currentValue) {
    return helper(currentValue, SyncedConfig::enable_break);
  }

  public static boolean getTeamLootEnabled(boolean currentValue) {
    return helper(currentValue, SyncedConfig::team_loot);
  }

  public static ResourceLocation getPinnedResolver(ResourceLocation currentValue) {
    return helper(currentValue, SyncedConfig::pinned_team_resolver);
  }
}
