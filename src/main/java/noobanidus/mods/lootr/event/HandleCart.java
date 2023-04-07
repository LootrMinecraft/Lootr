package noobanidus.mods.lootr.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.networking.OpenCart;
import noobanidus.mods.lootr.networking.PacketHandler;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class HandleCart {
  private static Set<Chunk> loadedChunks = Collections.newSetFromMap(new WeakHashMap<Chunk, Boolean>());

  @SubscribeEvent
  public static void chunkLoad(ChunkEvent.Load event) {
    loadedChunks.add(event.getChunk());
  }

  @SubscribeEvent
  public static void chunkUnload(ChunkEvent.Unload event) {
    loadedChunks.remove(event.getChunk());
  }

  @SubscribeEvent
  public static void onEntityJoin(EntityJoinWorldEvent event) {
    if (event.getEntity() instanceof EntityMinecartChest) {
      EntityMinecartChest chest = (EntityMinecartChest) event.getEntity();
      if (!chest.world.isRemote && chest.lootTable != null && ConfigManager.CONVERT_MINESHAFTS && !ConfigManager.isBlacklisted(chest.lootTable)) {
        LootrChestMinecartEntity lootr = new LootrChestMinecartEntity(chest.world, chest.posX, chest.posY, chest.posZ);
        lootr.setLootTable(chest.lootTable, chest.lootTableSeed);
        int chunkX = MathHelper.floor(chest.posX) >> 4;
        int chunkZ = MathHelper.floor(chest.posZ) >> 4;
        Chunk chunk = chest.world.getChunk(chunkX, chunkZ);
        if(loadedChunks.contains(chunk)) {
          chest.world.spawnEntity(lootr);
        } else {
          chunk.addEntity(lootr);
        }
        event.setCanceled(true);
        chest.dropContentsWhenDead = false;
        chest.setDead();
        //EntityTicker.addEntity(lootr);
      }
    }
  }

  @SubscribeEvent
  public static void onEntityTrack(PlayerEvent.StartTracking event) {
    Entity target = event.getTarget();
    if (target instanceof LootrChestMinecartEntity) {
      EntityPlayer player = event.getEntityPlayer();
      if (((LootrChestMinecartEntity) event.getTarget()).getOpeners().contains(player.getUniqueID())) {
        OpenCart cart = new OpenCart(event.getTarget().getEntityId());
        PacketHandler.sendToInternal(cart, (EntityPlayerMP) player);
      }
    }
  }
}
