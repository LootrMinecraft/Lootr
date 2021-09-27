package noobanidus.mods.lootr.ticker;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import java.util.ArrayList;
import java.util.List;

public class EntityTicker {
  private static final List<LootrChestMinecartEntity> entities = new ArrayList<>();

  public static void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      entities.removeIf(Entity::isAddedToWorld);
      for (LootrChestMinecartEntity entity : entities) {
        ServerLevel world = (ServerLevel) entity.level;
        ServerChunkCache provider = world.getChunkSource();
        ChunkAccess ichunk = provider.getChunk(Mth.floor(entity.getX() / 16.0D), Mth.floor(entity.getZ() / 16.0D), ChunkStatus.FULL, false);
        if (ichunk != null) {
          world.addFreshEntity(entity);
        }
      }
    }
  }

  public static void addEntity(LootrChestMinecartEntity entity) {
    entities.add(entity);
  }
}
