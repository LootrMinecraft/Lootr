package noobanidus.mods.lootr.neoforge.gen.builders;

import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;
import noobanidus.mods.lootr.neoforge.client.block.UnbakedCustomModel;

public class UnbakedCustomModelBuilder extends CustomBlockStateModelBuilder {
  private final UnbakedCustomModel model;

  public UnbakedCustomModelBuilder(UnbakedCustomModel model) {
    this.model = model;
  }

  @Override
  public UnbakedCustomModelBuilder with(VariantMutator variantMutator) {
    return this;
  }

  @Override
  public UnbakedCustomModelBuilder with(UnbakedMutator variantMutator) {
    return new UnbakedCustomModelBuilder(variantMutator.apply(model));
  }

  @Override
  public CustomUnbakedBlockStateModel toUnbaked() {
    return model;
  }
}
