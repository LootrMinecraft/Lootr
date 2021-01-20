package noobanidus.mods.lootr.ticker;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import java.util.ArrayList;
import java.util.List;

public class EntityTicker {
  private static final List<LootrChestMinecartEntity> entities = new ArrayList<>();

  public static void onServerTick (TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      entities.removeIf(Entity::isAddedToWorld);
      for (LootrChestMinecartEntity entity : entities) {
        ServerWorld world = (ServerWorld) entity.world;
        ServerChunkProvider provider = world.getChunkProvider();
        IChunk ichunk = provider.getChunk(MathHelper.floor(entity.getPosX() / 16.0D), MathHelper.floor(entity.getPosZ() / 16.0D), ChunkStatus.FULL, false);
        if (ichunk != null) {
          world.addEntity(entity);
        }
      }
    }
  }

  public static void addEntity (LootrChestMinecartEntity entity) {
    entities.add(entity);
  }
}
