package noobanidus.mods.lootr.entity;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.data.DataStorage;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid= LootrAPI.MODID)
public class EntityTicker {
  private static final List<LootrChestMinecartEntity> entities = new ArrayList<>();

  @SubscribeEvent
  public static void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      List<LootrChestMinecartEntity> completed = new ArrayList<>();
      for (LootrChestMinecartEntity entity : entities) {
        if (entity.isAddedToWorld()) {
          continue;
        }
        ServerLevel world = (ServerLevel) entity.level;
        ServerChunkCache provider = world.getChunkSource();
        if (provider.getChunkFuture(Mth.floor(entity.getX() / 16.0D), Mth.floor(entity.getZ() / 16.0D), ChunkStatus.FULL, false).isDone()) {
          world.addFreshEntity(entity);
          completed.add(entity);
        }
      }
      entities.removeAll(completed);
      DataStorage.doDecay();
      DataStorage.doRefresh();
    }
  }

  public static void addEntity(LootrChestMinecartEntity entity) {
    entities.add(entity);
  }
}
