package noobanidus.mods.lootr.events;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.ticker.EntityTicker;

public class HandleCart {
  public static void onEntityJoin (EntityJoinWorldEvent event) {
    if (event.getEntity().getType() == EntityType.CHEST_MINECART) {
      ChestMinecartEntity chest = (ChestMinecartEntity) event.getEntity();
      if (chest.lootTable != null && !chest.world.isRemote && ConfigManager.CONVERT_MINESHAFTS.get()) {
        LootrChestMinecartEntity lootr = new LootrChestMinecartEntity(chest.world, chest.getPosX(), chest.getPosY(), chest.getPosZ());
        lootr.setLootTable(chest.lootTable, chest.lootTableSeed);
        event.setCanceled(true);
        EntityTicker.addEntity(lootr);
      }
    }
  }
}
