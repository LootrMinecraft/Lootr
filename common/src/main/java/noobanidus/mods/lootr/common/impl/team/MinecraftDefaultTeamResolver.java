package noobanidus.mods.lootr.common.impl.team;

import com.google.auto.service.AutoService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PlayerContext;
import noobanidus.mods.lootr.common.api.team.ITeamResolver;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AutoService(ITeamResolver.class)
public class MinecraftDefaultTeamResolver implements ITeamResolver {
  private boolean cacheInitialized = false;
  private final Map<String, UUID> teamCache = new HashMap<>();

  private static MinecraftDefaultTeamResolver instance = null;

  public MinecraftDefaultTeamResolver() {
    if (instance != null) {
      LootrAPI.LOG.error("Created a new instance of the default team resolver when there was an existing instance!", new Exception());
    } else {
      instance = this;
    }
  }

  public static MinecraftDefaultTeamResolver getOrCreateInstance () {
    if (instance == null) {
      new MinecraftDefaultTeamResolver();
    }

    return instance;
  }

  public static void resetCache () {
    if (instance != null) {
      instance.cacheInitialized = false;
    }
  }

  private UUID getUuidForTeam(PlayerTeam team) {
    return teamCache.computeIfAbsent(team.getName(), name -> UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)));
  }

  private void initializeCache (Level level) {
    teamCache.clear();
    level.getScoreboard().getPlayerTeams().forEach(this::getUuidForTeam);
    cacheInitialized = true;
  }

  @Override
  public UUID resolvePlayer(Player player) {
    if (!cacheInitialized) {
      initializeCache(player.level());
    }
    var team = player.getTeam();
    if (team == null) {
      return player.getUUID();
    }

    return getUuidForTeam(team);
  }

  @Override
  public ResourceLocation resolverId() {
    return LootrAPI.MINECRAFT_TEAM_RESOLVER;
  }

  @Override
  public int priority() {
    return -1000;
  }
}
