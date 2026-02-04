package noobanidus.mods.lootr.neoforge.gen;

import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.model.Variant.SimpleModelState;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.LootrBarrelBlock;
import noobanidus.mods.lootr.neoforge.client.block.UnbakedCustomModel;
import noobanidus.mods.lootr.neoforge.gen.builders.UnbakedCustomModelBuilder;
import noobanidus.mods.lootr.neoforge.init.ModBlocks;

import java.util.stream.Stream;

public class LootrModelProvider extends ModelProvider {
  public LootrModelProvider(PackOutput output) {
    super(output, LootrAPI.MODID);
  }

  @Override
  protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
    var opened = new UnbakedCustomModel(LootrAPI.rl("block/lootr_opened_barrel"), LootrAPI.rl("block/lootr_barrel_unopened"), Identifier.withDefaultNamespace("block/barrel"), SimpleModelState.DEFAULT, false);
    var unopened = new UnbakedCustomModel(LootrAPI.rl("block/lootr_opened_barrel_open"), LootrAPI.rl("block/lootr_barrel_unopened_open"), Identifier.withDefaultNamespace("block/barrel_open"), SimpleModelState.DEFAULT, true);

    var baseVariant = MultiVariant.of(new UnbakedCustomModelBuilder(opened));

    blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.BARREL.get(), baseVariant)
        .withUnbaked(PropertyDispatch.modifyUnbaked(LootrBarrelBlock.FACING, LootrBarrelBlock.OPEN)
            .generate((facing, open) -> {
              Quadrant rotX = facing == Direction.UP ? Quadrant.R180 : Quadrant.R90;
              Quadrant rotY = facing.getAxis() != Direction.Axis.Y ? Quadrant.values()[(int) facing.toYRot() / 90] : Quadrant.R0;

              return UnbakedMutator.builder()
                  .add(UnbakedCustomModel.class, unbaked ->
                      open ? unopened.withState(unbaked.getState()
                          .withX(rotX).withY(rotY)) : opened.withState(unbaked.getState()
                          .withX(rotX).withY(rotY))).build();
            })));
  }

  @Override
  protected Stream<? extends Holder<Block>> getKnownBlocks() {
    return Stream.of(ModBlocks.BARREL);
  }
}
