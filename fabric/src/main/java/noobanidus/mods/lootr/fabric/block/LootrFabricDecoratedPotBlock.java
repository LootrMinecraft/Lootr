package noobanidus.mods.lootr.fabric.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.block.LootrDecoratedPotBlock;
import noobanidus.mods.lootr.fabric.block.entity.LootrFabricDecoratedPotBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LootrFabricDecoratedPotBlock extends LootrDecoratedPotBlock {
  public LootrFabricDecoratedPotBlock(Properties properties) {
    super(properties);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new LootrFabricDecoratedPotBlockEntity(blockPos, blockState);
  }
}
