package noobanidus.mods.lootr.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.server.FMLServerHandler;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.data.DataStorage;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class EntityTicker {
  private static final List<LootrChestMinecartEntity> entities = new ArrayList<>();

  @SubscribeEvent
  public static void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      if(FMLCommonHandler.instance().getMinecraftServerInstance() == null)
        return;
      entities.removeIf(Entity::isAddedToWorld);
      for (LootrChestMinecartEntity entity : entities) {
        WorldServer world = (WorldServer) entity.world;
        ChunkProviderServer provider = world.getChunkProvider();
        Chunk ichunk = provider.getLoadedChunk(MathHelper.floor(entity.posX / 16.0D), MathHelper.floor(entity.posZ / 16.0D));
        if (ichunk != null) {
          world.spawnEntity(entity);
        }
      }
      DataStorage.doDecay();
      DataStorage.doRefresh();
    }
  }

  public static void addEntity(LootrChestMinecartEntity entity) {
    entities.add(entity);
  }
}
