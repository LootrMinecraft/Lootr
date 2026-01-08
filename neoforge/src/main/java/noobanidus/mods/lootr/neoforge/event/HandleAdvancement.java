package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

@SuppressWarnings("resource")
@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleAdvancement {
  @SubscribeEvent
  public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
    if (!event.getEntity().level().isClientSide()) {
      LootrRegistry.getAdvancementTrigger().trigger((ServerPlayer) event.getEntity(), event.getAdvancement().id());
    }
  }
}
