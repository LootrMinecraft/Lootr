package noobanidus.mods.lootr.common.api.interfaces.advancement;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public interface IAdvancementTrigger extends ITrigger {
  void trigger(ServerPlayer player, Identifier advancementId);
}
