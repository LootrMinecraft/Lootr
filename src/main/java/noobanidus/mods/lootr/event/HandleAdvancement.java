package noobanidus.mods.lootr.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.init.ModAdvancements;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleAdvancement {
  @SubscribeEvent
  public static void onAdvancement(AdvancementEvent event) {
    if (!event.getEntity().level().isClientSide()) {
      ModAdvancements.ADVANCEMENT_PREDICATE.trigger((ServerPlayer) event.getEntity(), event.getAdvancement().getId());
    }
  }
}
