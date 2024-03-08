package noobanidus.mods.lootr.entity;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.DataStorage;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid= LootrAPI.MODID)
public class EntityTicker {
  private static final List<LootrChestMinecartEntity> entities = new ArrayList<>();
  private static final List<LootrChestMinecartEntity> pendingEntities = new ArrayList<>();

  private final static Object listLock = new Object();

  private final static Object worldLock = new Object();

  private static boolean tickingList = false;

  @SubscribeEvent
  public static void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      if (!ConfigManager.DISABLE.get()) {
        List<LootrChestMinecartEntity> completed = new ArrayList<>();
        List<LootrChestMinecartEntity> copy;
        synchronized (listLock) {
          tickingList = true;
          copy = new ArrayList<>(entities);
          tickingList = false;
        }
        synchronized (worldLock) {
          for (LootrChestMinecartEntity entity : copy) {
            if (entity.isAddedToWorld()) {
              continue;
            }
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
  }

  public static void addEntity(LootrChestMinecartEntity entity) {
    if (ConfigManager.DISABLE.get()) {
      return;
    }
    synchronized (listLock) {
      if(tickingList)
        pendingEntities.add(entity);
      else
        entities.add(entity);
    }
  }
}
