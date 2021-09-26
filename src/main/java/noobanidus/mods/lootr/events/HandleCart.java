package noobanidus.mods.lootr.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.networking.OpenCart;
import noobanidus.mods.lootr.networking.PacketHandler;
import noobanidus.mods.lootr.ticker.EntityTicker;

public class HandleCart {
  public static void onEntityJoin (EntityJoinWorldEvent event) {
    if (event.getEntity().getType() == EntityType.CHEST_MINECART) {
      ChestMinecartEntity chest = (ChestMinecartEntity) event.getEntity();
      if (!chest.level.isClientSide && chest.lootTable != null && ConfigManager.CONVERT_MINESHAFTS.get() && !ConfigManager.getLootBlacklist().contains(chest.lootTable)) {
        LootrChestMinecartEntity lootr = new LootrChestMinecartEntity(chest.level, chest.getX(), chest.getY(), chest.getZ());
        lootr.setLootTable(chest.lootTable, chest.lootTableSeed);
        event.setCanceled(true);
        EntityTicker.addEntity(lootr);
      }
    }
  }

  public static void onEntityTrack (PlayerEvent.StartTracking event) {
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
