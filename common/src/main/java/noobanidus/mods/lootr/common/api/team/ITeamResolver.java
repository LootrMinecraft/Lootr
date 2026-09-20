package noobanidus.mods.lootr.common.api.team;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface ITeamResolver {
  UUID resolveServerPlayer(Player player);

  UUID resolveClientPlayer(Player player);

  ResourceLocation resolverId();

  default int priority() {
    return 0;
  }
}
