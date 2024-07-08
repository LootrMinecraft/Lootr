package noobanidus.mods.lootr.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.registry.LootrRegistry;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleAdvancement {
  @SubscribeEvent
  public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
    if (!event.getEntity().level().isClientSide()) {
      LootrRegistry.getAdvancementTrigger().trigger((ServerPlayer) event.getEntity(), event.getAdvancement().id());
    }
  }

  @SubscribeEvent
  public static void onAdvancement(AdvancementEvent.AdvancementProgressEvent event) {
    if (!event.getEntity().level().isClientSide()) {
      LootrRegistry.getAdvancementTrigger().trigger((ServerPlayer) event.getEntity(), event.getAdvancement().id());
    }
  }
}
