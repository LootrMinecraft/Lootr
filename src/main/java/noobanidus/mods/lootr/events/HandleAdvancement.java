package noobanidus.mods.lootr.events;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import noobanidus.mods.lootr.Lootr;

public class HandleAdvancement {
  public static void onAdvancement (AdvancementEvent event) {
    if (!event.getPlayer().level.isClientSide) {
      Lootr.ADVANCEMENT_PREDICATE.trigger((ServerPlayerEntity) event.getPlayer(), event.getAdvancement().getId());
    }
  }
}
