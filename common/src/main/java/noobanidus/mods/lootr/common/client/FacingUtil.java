package noobanidus.mods.lootr.common.client;

import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;

public class FacingUtil {
  public static ModelState transformFor (Direction facing) {
    Variant.SimpleModelState state = switch (facing) {
      case DOWN -> new Variant.SimpleModelState(Quadrant.R180, Quadrant.R0, Quadrant.R0, false);
      case UP -> Variant.SimpleModelState.DEFAULT;
      case NORTH -> new Variant.SimpleModelState(Quadrant.R90, Quadrant.R0, Quadrant.R0, false);
      case SOUTH -> new Variant.SimpleModelState(Quadrant.R90, Quadrant.R180, Quadrant.R0, false);
      case WEST -> new Variant.SimpleModelState(Quadrant.R90, Quadrant.R270, Quadrant.R0, false);
      case EAST -> new Variant.SimpleModelState(Quadrant.R90, Quadrant.R90, Quadrant.R0, false);
    };
    return state.asModelState();
  }
}
