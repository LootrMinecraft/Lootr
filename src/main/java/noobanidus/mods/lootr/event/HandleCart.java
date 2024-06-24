package noobanidus.mods.lootr.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.EntityTicker;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleCart {
  @SubscribeEvent
  public static void onEntityJoin(EntityJoinLevelEvent event) {
    if (ConfigManager.isDimensionBlocked(event.getLevel().dimension()) || ConfigManager.DISABLE.get()) {
      return;
    }
    if (event.getEntity().getType() == EntityType.CHEST_MINECART && event.getEntity() instanceof MinecartChest chest) {
      if (!chest.level().isClientSide && chest.lootTable != null && ConfigManager.CONVERT_MINESHAFTS.get() && !ConfigManager.getLootBlacklist().contains(chest.lootTable)) {
        if (chest.level() instanceof ServerLevel level) {
          LootrChestMinecartEntity lootr = new LootrChestMinecartEntity(chest.level(), chest.getX(), chest.getY(), chest.getZ());
          lootr.setLootTable(chest.lootTable, chest.lootTableSeed);
          lootr.getPersistentData().merge(chest.getPersistentData());
          event.setCanceled(true);
          if (!level.getServer().isSameThread()) {
            // TODO: If this is actually triggering, we need to ticket the chunk. The only instance I can think of this happening is if a mod is manually creating this event, as the Forge defaults only fire it in the main thread.
            LootrAPI.LOG.error("Minecart with Loot table created off main thread. Falling back on EntityTicker.");
            EntityTicker.addEntity(lootr);
          } else {
            level.addFreshEntity(lootr);
          }
        }
      }
    }
  }
}
