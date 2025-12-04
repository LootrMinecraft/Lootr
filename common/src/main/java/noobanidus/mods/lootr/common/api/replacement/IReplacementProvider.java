package noobanidus.mods.lootr.common.api.replacement;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IReplacementProvider extends Function<BlockState, @Nullable BlockState> {
  TagKey<Block> getApplicableTag();

  @Override
  default @Nullable BlockState apply(BlockState state) {
    if (state.isAir() || !state.is(getApplicableTag())) {
      return null;
    }

    return getReplacement(state);
  }

  @Nullable
  BlockState getReplacement(BlockState state);
}
