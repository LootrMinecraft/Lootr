package noobanidus.mods.lootr.common.api.interfaces.advancement;

import net.minecraft.server.level.ServerPlayer;

public interface ITrapdoorTrigger extends ITrigger {
  void trigger(ServerPlayer player);
}
