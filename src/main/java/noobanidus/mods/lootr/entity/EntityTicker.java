package noobanidus.mods.lootr.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import noobanidus.mods.lootr.data.DataStorage;

import java.util.ArrayList;
import java.util.List;

public class EntityTicker {
  private static final List<LootrChestMinecartEntity> entities = new ArrayList<>();

  public static void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      entities.removeIf(Entity::isAddedToWorld);
      for (LootrChestMinecartEntity entity : entities) {
        ServerWorld world = (ServerWorld) entity.level;
        ServerChunkProvider provider = world.getChunkSource();
        IChunk ichunk = provider.getChunk(MathHelper.floor(entity.getX() / 16.0D), MathHelper.floor(entity.getZ() / 16.0D), ChunkStatus.FULL, false);
        if (ichunk != null) {
          world.addFreshEntity(entity);
        }
      }
      DataStorage.doDecay(event);
    }
  }

  public static void addEntity(LootrChestMinecartEntity entity) {
    entities.add(entity);
  }
}
