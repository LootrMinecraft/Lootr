package noobanidus.mods.lootr.event;

<<<<<<< HEAD
=======
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
>>>>>>> 28fbef8 (Test for off-thread cart creation.)
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.PermissionAPI;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.EntityTicker;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleCart {
  @SubscribeEvent
  public static void onEntityJoin(EntityJoinLevelEvent event) {
    if (ConfigManager.isDimensionBlocked(event.getLevel().dimension()) || ConfigManager.DISABLE.get()) {
      return;
    }
    if (event.getEntity().getType() == EntityType.CHEST_MINECART && event.getEntity() instanceof MinecartChest chest) {
      if (!chest.level().isClientSide && chest.lootTable != null && ConfigManager.CONVERT_MINESHAFTS.get() && !ConfigManager.getLootBlacklist().contains(chest.lootTable)) {
        if (chest.level() instanceof ServerLevel level) {
          LootrChestMinecartEntity lootr = new LootrChestMinecartEntity(chest.level, chest.getX(), chest.getY(), chest.getZ());
          lootr.setLootTable(chest.lootTable, chest.lootTableSeed);
          event.setCanceled(true);
          if (!level.getServer().isSameThread()) {
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
