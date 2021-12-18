package noobanidus.mods.lootr.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import noobanidus.mods.lootr.Lootr;

public class HandleAdvancement {
  public static void onAdvancement(AdvancementEvent event) {
    if (!event.getPlayer().level.isClientSide) {
      Lootr.ADVANCEMENT_PREDICATE.trigger((ServerPlayer) event.getPlayer(), event.getAdvancement().getId());
    }
  }
}
