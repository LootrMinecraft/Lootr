package noobanidus.mods.lootr.neoforge.gen.builders;

import com.mojang.math.Quadrant;
import noobanidus.mods.lootr.common.client.block.UnbakedCustomModel;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface UnbakedCustomMutator extends UnaryOperator<UnbakedCustomModel> {
  UnbakedCustomMutator.UnbakedCustomModelProperty<Quadrant> X_ROT = UnbakedCustomModel::withXRot;
  UnbakedCustomMutator.UnbakedCustomModelProperty<Quadrant> Y_ROT = UnbakedCustomModel::withYRot;
  UnbakedCustomMutator.UnbakedCustomModelProperty<Quadrant> Z_ROT = UnbakedCustomModel::withZRot;
  UnbakedCustomMutator.UnbakedCustomModelProperty<Boolean> UV_LOCK = UnbakedCustomModel::withUvLock;

  default UnbakedCustomMutator then(UnbakedCustomMutator mutator) {
    return p_405783_ -> mutator.apply(this.apply(p_405783_));
  }

  @FunctionalInterface
  interface UnbakedCustomModelProperty<T> {
    UnbakedCustomModel apply(UnbakedCustomModel variant, T value);

    default UnbakedCustomMutator withValue(T value) {
      return p_405243_ -> this.apply(p_405243_, value);
    }
  }
}
