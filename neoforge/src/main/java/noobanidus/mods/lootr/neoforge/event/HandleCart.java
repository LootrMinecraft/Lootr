package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.entity.EntityTicker;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.neoforge.init.ModTicketTypes;

// TODO: Centralize this
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
    if (entity.getType().is(LootrTags.Entity.CONVERT_BLACKLIST)) {
      return;
    }
    if (entity.getType().is(LootrTags.Entity.CONVERT_ENTITIES) && entity instanceof AbstractMinecartContainer cart) {
      if (cart.getContainerLootTable() == null || LootrAPI.isLootTableBlacklisted(cart.getContainerLootTable())) {
        return;
      }
      @SuppressWarnings("unchecked") LootrChestMinecartEntity lootrCart = new LootrChestMinecartEntity((EntityType<LootrChestMinecartEntity>) LootrRegistry.getMinecart(), cart.level());
      lootrCart.setPos(cart.getX(), cart.getY(), cart.getZ());
      PlatformAPI.copyEntityData(cart, lootrCart);
      event.setCanceled(true);
      if (!level.getServer().isSameThread()) {
        level.getServer().execute(() -> {
          level.getChunkSource().addTicket(new Ticket(ModTicketTypes.ENTITY_TICKET_TYPE.get(), ChunkMap.FORCED_TICKET_LEVEL), lootrCart.chunkPosition());
        });
        LootrAPI.LOG.error("Minecart with Loot table created off main thread. Falling back on EntityTicker.");
        EntityTicker.addEntity(lootrCart);
      } else {
        level.addFreshEntity(lootrCart);
      }
    }
  }
}
