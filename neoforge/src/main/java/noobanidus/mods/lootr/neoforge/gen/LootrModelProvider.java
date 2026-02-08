package noobanidus.mods.lootr.neoforge.gen;

import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.client.ConfigDisplayType;
import noobanidus.mods.lootr.common.block.LootrBarrelBlock;
import noobanidus.mods.lootr.common.client.select.SelectConfigType;
import noobanidus.mods.lootr.common.client.special.LootrChestSpecialRenderer;
import noobanidus.mods.lootr.common.client.special.LootrDecoratedPotSpecialRenderer;
import noobanidus.mods.lootr.common.client.special.LootrShulkerSpecialRenderer;
import noobanidus.mods.lootr.neoforge.client.block.UnbakedBrushableModel;
import noobanidus.mods.lootr.neoforge.client.block.UnbakedCustomModel;
import noobanidus.mods.lootr.neoforge.gen.builders.UnbakedBrushableModelBuilder;
import noobanidus.mods.lootr.neoforge.gen.builders.UnbakedCustomModelBuilder;
import noobanidus.mods.lootr.neoforge.init.ModBlocks;
import noobanidus.mods.lootr.neoforge.init.ModItems;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class LootrModelProvider extends ModelProvider {
  public LootrModelProvider(PackOutput output) {
    super(output, LootrAPI.MODID);
  }

  @Override
  protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
    var opened = new UnbakedCustomModel(LootrAPI.rl("block/lootr_opened_barrel"), LootrAPI.rl("block/lootr_barrel_unopened"), Identifier.withDefaultNamespace("block/barrel"), Variant.SimpleModelState.DEFAULT, false);
    var unopened = new UnbakedCustomModel(LootrAPI.rl("block/lootr_opened_barrel_open"), LootrAPI.rl("block/lootr_barrel_unopened_open"), Identifier.withDefaultNamespace("block/barrel_open"), Variant.SimpleModelState.DEFAULT, true);

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

    var sandBase = new UnbakedBrushableModel(LootrAPI.rl("block/suspicious_sand_open"), Identifier.withDefaultNamespace("block/suspicious_sand_0"), Identifier.withDefaultNamespace("block/suspicious_sand_1"), Identifier.withDefaultNamespace("block/suspicious_sand_2"), Identifier.withDefaultNamespace("block/suspicious_sand_3"), Variant.SimpleModelState.DEFAULT);

    var sandVariants = MultiVariant.of(new UnbakedBrushableModelBuilder(sandBase));

    blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.SUSPICIOUS_SAND.get(), sandVariants)
        .withUnbaked(PropertyDispatch.modifyUnbaked(BlockStateProperties.DUSTED)
            .generate(stage -> UnbakedMutator.builder()
                .add(UnbakedBrushableModel.class, unbaked -> unbaked)
                .build())));

    var gravelBase = new UnbakedBrushableModel(LootrAPI.rl("block/suspicious_gravel_open"), Identifier.withDefaultNamespace("block/suspicious_gravel_0"), Identifier.withDefaultNamespace("block/suspicious_gravel_1"), Identifier.withDefaultNamespace("block/suspicious_gravel_2"), Identifier.withDefaultNamespace("block/suspicious_gravel_3"), Variant.SimpleModelState.DEFAULT);
    var gravelVariants = MultiVariant.of(new UnbakedBrushableModelBuilder(gravelBase));
    blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.SUSPICIOUS_GRAVEL.get(), gravelVariants)
        .withUnbaked(PropertyDispatch.modifyUnbaked(BlockStateProperties.DUSTED)
            .generate(stage -> UnbakedMutator.builder()
                .add(UnbakedBrushableModel.class, unbaked -> unbaked)
                .build())));

    itemModels.itemModelOutput.accept(
        ModItems.BARREL.get(),
        new SelectItemModel.Unbaked(
            new SelectItemModel.UnbakedSwitch<>(
                new SelectConfigType(),
                List.of(
                    new SelectItemModel.SwitchCase<>(
                        List.of(ConfigDisplayType.VANILLA),
                        new BlockModelWrapper.Unbaked(
                            Identifier.withDefaultNamespace("block/barrel"),
                            Collections.emptyList()
                        )),
                    new SelectItemModel.SwitchCase<>(
                        List.of(ConfigDisplayType.DEFAULT),
                        new BlockModelWrapper.Unbaked(
                            LootrAPI.rl("block/lootr_barrel_unopened"),
                            Collections.emptyList()))
                )
            ),
            Optional.of(
                new BlockModelWrapper.Unbaked(
                    LootrAPI.rl("block/lootr_barrel_unopened"),
                    Collections.emptyList()))
        )
    );

    itemModels.itemModelOutput.accept(
        ModItems.SUSPICIOUS_GRAVEL.get(),
        new BlockModelWrapper.Unbaked(
            Identifier.withDefaultNamespace("block/suspicious_gravel_0"),
            Collections.emptyList())
    );
    itemModels.itemModelOutput.accept(
        ModItems.SUSPICIOUS_SAND.get(),
        new BlockModelWrapper.Unbaked(
            Identifier.withDefaultNamespace("block/suspicious_sand_0"),
            Collections.emptyList())
    );
    itemModels.itemModelOutput.accept(
        ModItems.CHEST.get(),
        new SpecialModelWrapper.Unbaked(
            Identifier.withDefaultNamespace("item/chest"),
            new LootrChestSpecialRenderer.Unbaked(
                LootrAPI.rl("chest"),
                Identifier.withDefaultNamespace("entity/chest/normal")
            )
        )
    );
    itemModels.itemModelOutput.accept(
        ModItems.TRAPPED_CHEST.get(),
        new SpecialModelWrapper.Unbaked(
            Identifier.withDefaultNamespace("item/chest"),
            new LootrChestSpecialRenderer.Unbaked(
                LootrAPI.rl("chest_trapped"),
                Identifier.withDefaultNamespace("entity/chest/trapped")
            )
        )
    );
    itemModels.itemModelOutput.accept(
        ModItems.INVENTORY.get(),
        new SpecialModelWrapper.Unbaked(
            Identifier.withDefaultNamespace("item/chest"),
            new LootrChestSpecialRenderer.Unbaked(
                LootrAPI.rl("chest"),
                Identifier.withDefaultNamespace("entity/chest/normal")
            )
        )
    );
    itemModels.itemModelOutput.accept(
        ModItems.SHULKER.get(),
        new SpecialModelWrapper.Unbaked(
            Identifier.withDefaultNamespace("item/shulker_box"),
            new LootrShulkerSpecialRenderer.Unbaked(
                LootrAPI.rl("shulker")
            )
        )
    );
    itemModels.itemModelOutput.accept(
        ModItems.DECORATED_POT.get(),
        new SpecialModelWrapper.Unbaked(
            Identifier.withDefaultNamespace("item/decorated_pot"),
            LootrDecoratedPotSpecialRenderer.Unbaked.decoratedPot()
        )
    );
    itemModels.itemModelOutput.accept(
        ModItems.TROPHY.get(),
        new BlockModelWrapper.Unbaked(
            LootrAPI.rl("block/trophy"),
            Collections.emptyList()
        )
    );
  }

  @Override
  protected Stream<? extends Holder<Block>> getKnownBlocks() {
    return Stream.of(ModBlocks.BARREL, ModBlocks.SUSPICIOUS_GRAVEL, ModBlocks.SUSPICIOUS_SAND);
  }
}
