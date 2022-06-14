package noobanidus.mods.lootr.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.EntityTicker;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.networking.OpenCart;
import noobanidus.mods.lootr.networking.PacketHandler;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class HandleCart {
  @SubscribeEvent
  public static void onEntityJoin(EntityJoinWorldEvent event) {
    if (event.getEntity() instanceof EntityMinecartChest) {
      EntityMinecartChest chest = (EntityMinecartChest) event.getEntity();
      if (!chest.world.isRemote && chest.lootTable != null && ConfigManager.CONVERT_MINESHAFTS && !ConfigManager.isBlacklisted(chest.lootTable)) {
        LootrChestMinecartEntity lootr = new LootrChestMinecartEntity(chest.world, chest.posX, chest.posY, chest.posZ);
        lootr.setLootTable(chest.lootTable, chest.lootTableSeed);
        chest.world.spawnEntity(lootr);
        event.setCanceled(true);
        EntityTicker.addEntity(lootr);
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
