package noobanidus.mods.lootr.entity;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.DataStorage;

import java.util.ArrayList;
import java.util.List;

public class EntityTicker {
  private static final List<LootrChestMinecartEntity> entities = new ArrayList<>();
  private static final List<LootrChestMinecartEntity> pendingEntities = new ArrayList<>();

  private final static Object listLock = new Object();

  private final static Object worldLock = new Object();

  private static boolean tickingList = false;

  public static void serverTick() {
    if (!ConfigManager.get().conversion.disable) {
      List<LootrChestMinecartEntity> completed = new ArrayList<>();
      List<LootrChestMinecartEntity> copy;
      synchronized (listLock) {
        tickingList = true;
        copy = new ArrayList<>(entities);
        tickingList = false;
      }
      synchronized (worldLock) {
        for (LootrChestMinecartEntity entity : copy) {
          ServerLevel world = (ServerLevel) entity.level();
          ServerChunkCache provider = world.getChunkSource();
          if (provider.hasChunk(Mth.floor(entity.getX() / 16.0D), Mth.floor(entity.getZ() / 16.0D))) {
            world.addFreshEntity(entity);
            completed.add(entity);
          }
        }
      }
      synchronized (listLock) {
        tickingList = true;
        entities.removeAll(completed);
        entities.addAll(pendingEntities);
        tickingList = false;
        pendingEntities.clear();
      }
    }
    DataStorage.doDecay();
    DataStorage.doRefresh();
  }

  public static void addEntity(LootrChestMinecartEntity entity) {
    if (ConfigManager.get().conversion.disable) {
      return;
    }
    synchronized (listLock) {
      if (tickingList)
        pendingEntities.add(entity);
      else
        entities.add(entity);
    }
  }
}