package noobanidus.mods.lootr.neoforge.gen.builders;

import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;
import noobanidus.mods.lootr.neoforge.client.block.UnbakedBrushableModel;

public class UnbakedBrushableModelBuilder extends CustomBlockStateModelBuilder {
  private final UnbakedBrushableModel model;

  public UnbakedBrushableModelBuilder(UnbakedBrushableModel model) {
    this.model = model;
  }

  @Override
  public UnbakedBrushableModelBuilder with(VariantMutator variantMutator) {
    return this;
  }

  @Override
  public UnbakedBrushableModelBuilder with(UnbakedMutator variantMutator) {
    return new UnbakedBrushableModelBuilder(variantMutator.apply(model));
  }

  @Override
  public CustomUnbakedBlockStateModel toUnbaked() {
    return model;
  }
}