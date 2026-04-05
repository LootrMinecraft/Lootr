package noobanidus.mods.lootr.common.api.integration.brushing;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

/**
 * This uses IBrushable$ as a prefix to disambiguate from potential
 * remap conflicts even though I have no idea what names yarn uses.
 */
public interface IBrushable {
  boolean IBrushable$brush(long l, Player player, Direction direction);

  void IBrushable$checkReset();
}
