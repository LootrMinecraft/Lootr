package noobanidus.mods.lootr.neoforge.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import noobanidus.mods.lootr.common.block.entity.LootrBarrelBlockEntity;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;
import org.jetbrains.annotations.NotNull;

public class LootrNeoForgeBarrelBlockEntity extends LootrBarrelBlockEntity {
  public LootrNeoForgeBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(pWorldPosition, pBlockState);
  }

  private ModelData modelData = null;

  @NotNull
  @Override
  public ModelData getModelData() {
    if (modelData == null) {
      modelData = ModelData.builder().with(ModBlockProperties.OPENED, false).build();
    }
    if (hasClientOpened()) {
      return modelData.derive().with(ModBlockProperties.OPENED, true).build();
    } else {
      return modelData.derive().with(ModBlockProperties.OPENED, false).build();
    }
  }
}
