package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.common.entity.EntityTicker;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleCart {
  @SubscribeEvent
  public static void onEntityJoin(EntityJoinLevelEvent event) {
    if (!(event.getLevel() instanceof ServerLevel level)) {
      return;
    }
    if (LootrAPI.isDimensionBlocked(level.dimension()) || LootrAPI.isDisabled()) {
      return;
    }
    // TODO: Conversion tag
    if (event.getEntity().getType() == EntityType.CHEST_MINECART && event.getEntity() instanceof MinecartChest chest) {
      if (!level.isClientSide() && chest.getLootTable() != null && LootrAPI.shouldConvertMineshafts() && !LootrAPI.isLootTableBlacklisted(chest.getLootTable())) {
        LootrChestMinecartEntity lootrCart = new LootrChestMinecartEntity(chest.level(), chest.getX(), chest.getY(), chest.getZ());
        lootrCart.setLootTable(chest.getLootTable(), chest.getLootTableSeed());
        lootrCart.getPersistentData().merge(chest.getPersistentData());
        event.setCanceled(true);
        if (!level.getServer().isSameThread()) {
          // TODO: If this is actually triggering, we need to ticket the chunk. The only instance I can think of this happening is if a mod is manually creating this event, as the Forge defaults only fire it in the main thread.
          LootrAPI.LOG.error("Minecart with Loot table created off main thread. Falling back on EntityTicker.");
          EntityTicker.addEntity(lootrCart);
        } else {
          level.addFreshEntity(lootrCart);
        }
      }
    }
  }
}
