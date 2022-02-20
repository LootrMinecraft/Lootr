package noobanidus.mods.lootr.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.data.DataStorage;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class EntityTicker {
  private static final List<LootrChestMinecartEntity> ENTITIES = new ObjectArrayList<>();

  @SubscribeEvent
  public static void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      ENTITIES.removeIf(Entity::isAddedToWorld);
      for (LootrChestMinecartEntity entity : ENTITIES) {
        ServerWorld world = (ServerWorld) entity.level;
        ServerChunkProvider provider = world.getChunkSource();
        IChunk ichunk = provider.getChunk(MathHelper.floor(entity.getX() / 16.0D), MathHelper.floor(entity.getZ() / 16.0D), ChunkStatus.FULL, false);
        if (ichunk != null) {
          world.addFreshEntity(entity);
        }
      }
    }
  }

  public static void addEntity(LootrChestMinecartEntity entity) {
    ENTITIES.add(entity);
  }
}
