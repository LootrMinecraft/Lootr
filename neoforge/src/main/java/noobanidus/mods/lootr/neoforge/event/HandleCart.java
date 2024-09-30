package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.entity.EntityTicker;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleCart {
  @SubscribeEvent
  public static void onEntityJoin(EntityJoinLevelEvent event) {
    if (!(event.getLevel() instanceof ServerLevel level) || level.isClientSide()) {
      return;
    }
    if (LootrAPI.isDimensionBlocked(level.dimension()) || LootrAPI.isDisabled()) {
      return;
    }
    Entity entity = event.getEntity();
    if (entity.getType().is(LootrTags.Entity.CONVERT_ENTITIES) && entity instanceof AbstractMinecartContainer cart) {
      if (cart.getLootTable() == null || LootrAPI.isLootTableBlacklisted(cart.getLootTable())) {
        return;
      }
      LootrChestMinecartEntity lootrCart = new LootrChestMinecartEntity(cart.level(), cart.getX(), cart.getY(), cart.getZ());
      PlatformAPI.copyEntityData(cart, lootrCart);
      event.setCanceled(true);
      if (!level.getServer().isSameThread()) {
        level.getChunkSource().addRegionTicket(LootrAPI.LOOTR_ENTITY_TICK_TICKET, lootrCart.chunkPosition(), 1, Unit.INSTANCE);
        LootrAPI.LOG.error("Minecart with Loot table created off main thread. Falling back on EntityTicker.");
        EntityTicker.addEntity(lootrCart);
      } else {
        level.addFreshEntity(lootrCart);
      }
    }
  }
}
