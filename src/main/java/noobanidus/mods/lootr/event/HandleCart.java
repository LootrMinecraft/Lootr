package noobanidus.mods.lootr.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.EntityTicker;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.networking.OpenCart;
import noobanidus.mods.lootr.networking.PacketHandler;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class HandleCart {
  @SubscribeEvent
  public static void onEntityJoin(EntityJoinWorldEvent event) {
    if (event.getEntity().getType() == EntityType.CHEST_MINECART) {
      ChestMinecartEntity chest = (ChestMinecartEntity) event.getEntity();
      if (!chest.level.isClientSide && chest.lootTable != null && ConfigManager.CONVERT_MINESHAFTS.get() && !ConfigManager.isBlacklisted(chest.lootTable)) {
        LootrChestMinecartEntity lootr = new LootrChestMinecartEntity(chest.level, chest.getX(), chest.getY(), chest.getZ());
        lootr.setLootTable(chest.lootTable, chest.lootTableSeed);
        event.setCanceled(true);
        EntityTicker.addEntity(lootr);
      }
    }
  }

  @SubscribeEvent
  public static void onEntityTrack(PlayerEvent.StartTracking event) {
    Entity target = event.getTarget();
    if (target.getType() == ModEntities.LOOTR_MINECART_ENTITY) {
      PlayerEntity player = event.getPlayer();
      if (((LootrChestMinecartEntity) event.getTarget()).getOpeners().contains(player.getUUID())) {
        OpenCart cart = new OpenCart(event.getTarget().getId());
        PacketHandler.sendToInternal(cart, (ServerPlayerEntity) player);
      }
    }
  }
}
