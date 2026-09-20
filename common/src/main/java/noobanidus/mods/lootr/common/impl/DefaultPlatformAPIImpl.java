package noobanidus.mods.lootr.common.impl;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import noobanidus.mods.lootr.common.api.IPlatformAPI;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;

import java.util.List;
import java.util.Set;

public abstract class DefaultPlatformAPIImpl implements IPlatformAPI {
  public static final Set<String> SERVER_ENVIRONMENT_REDUCE_FILES = Set.of(
      "ATERNOS_SERVER_ID",
      "EXAROTON_SERVER_ID",
      "LOOTR_REDUCE_DATA_FILE_QUANTITY"
  );

  private final boolean doesServerNeedLessFiles;

  public DefaultPlatformAPIImpl() {
    boolean serverNeedsLessFiles = false;

    for (String name : SERVER_ENVIRONMENT_REDUCE_FILES) {
      if (System.getenv(name) != null) {
        LootrAPI.LOG.info("Environment variable '{}' detected. If the save mode configuration is set to 'SMART', Lootr will only save data files for containers that have been opened by players, rather than every file.", name);
        serverNeedsLessFiles = true;
        break;
      }
    }

    this.doesServerNeedLessFiles = serverNeedsLessFiles;
  }

  @Override
  public boolean shouldDoInitialSave() {
    return false;
  }

  @Override
  public void syncAfterTeamChange(PlayerTeam playerTeam) {
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      return;
    }

    server.getPlayerList().getPlayers().forEach(player -> {
      if (playerTeam.equals(player.getTeam())) {
        PlatformAPI.syncAfterTeamChange(player);
      }
    });
  }

  @Override
  public void syncAfterTeamChange (String username) {
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      return;
    }

    ServerPlayer player = server.getPlayerList().getPlayerByName(username);
    if (player != null) {
      PlatformAPI.syncAfterTeamChange(player);
    }
  }

  protected Pair<List<Integer>, List<Integer>> getSyncData (Player player) {
    int radius = player.getServer().getPlayerList().getViewDistance() * 16;
    Vec3 center = player.getEyePosition();
    AABB box = new AABB(center.add(radius, radius, radius), center.subtract(radius, radius, radius));

    IntList open = new IntArrayList();
    IntList closed = new IntArrayList();

    for (Entity e : player.level().getEntities((Entity)null, box, (e) -> e.getType().is(LootrTags.Entity.CONTAINERS))) {
      if ((LootrAPI.resolveEntity(e) instanceof ILootrEntity entity)) {
        if (entity.hasVisualOpened(player)) {
          open.add(e.getId());
        } else {
          closed.add(e.getId());
        }
      }
    }

    return new Pair<>(open, closed);
  }
}
