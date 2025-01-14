package noobanidus.mods.lootr.common.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IReplacementProvider {
  @Nullable
  Block replacementBlockFor(Block original, BlockEntity blockEntity);

  @Nullable
  StateMapper stateMapperForBlock (Block original);

  @FunctionalInterface
  interface StateMapper {
    @Nullable
    BlockState map(BlockState newState, BlockState originalState, BlockEntity blockEntity);
  }
}
