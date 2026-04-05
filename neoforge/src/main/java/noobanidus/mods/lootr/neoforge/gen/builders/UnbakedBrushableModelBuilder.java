package noobanidus.mods.lootr.neoforge.gen.builders;

import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;
import noobanidus.mods.lootr.neoforge.client.block.UnbakedBrushableModel;
import org.jspecify.annotations.NonNull;

public class UnbakedBrushableModelBuilder extends CustomBlockStateModelBuilder {
  private final UnbakedBrushableModel model;

  public UnbakedBrushableModelBuilder(UnbakedBrushableModel model) {
    this.model = model;
  }

  @Override
  public @NonNull UnbakedBrushableModelBuilder with(@NonNull VariantMutator variantMutator) {
    return this;
  }

  @Override
  public @NonNull UnbakedBrushableModelBuilder with(UnbakedMutator variantMutator) {
    return new UnbakedBrushableModelBuilder(variantMutator.apply(model));
  }

  @Override
  public @NonNull CustomUnbakedBlockStateModel toUnbaked() {
    return model;
  }
}