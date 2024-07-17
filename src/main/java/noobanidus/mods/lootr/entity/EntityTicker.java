package noobanidus.mods.lootr.entity;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.ArrayList;
import java.util.List;


public class EntityTicker {
  private static final List<LootrChestMinecartEntity> entities = new ArrayList<>();
  private static final List<LootrChestMinecartEntity> pendingEntities = new ArrayList<>();

  private final static Object listLock = new Object();

  private final static Object levelLock = new Object();

  private static boolean tickingList = false;

  public static void onServerTick() {
    if (LootrAPI.isDisabled()) {
      return;
    }
    List<LootrChestMinecartEntity> completed = new ArrayList<>();
    List<LootrChestMinecartEntity> copy;
    synchronized (listLock) {
      tickingList = true;
      copy = new ArrayList<>(entities);
      tickingList = false;
    }
    synchronized (levelLock) {
      for (LootrChestMinecartEntity entity : copy) {
        // TODO: Forge-only
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

  public static void addEntity(LootrChestMinecartEntity entity) {
    if (LootrAPI.isDisabled()) {
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
