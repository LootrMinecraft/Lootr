package noobanidus.mods.lootr.common.api.replacement;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface ILootrBlockReplacementProvider extends Function<Block, @Nullable Block> {
  default int getPriority () {
    return 0;
  }

  TagKey<Block> getApplicableTag();

  Block getBlock ();

  @Override
  default @Nullable Block apply(Block block) {
    if (block == getBlock()) {
      return null;
    }
    BlockState defaultState = block.defaultBlockState();
    if (defaultState.isAir() || !defaultState.is(getApplicableTag())) {
      return null;
    }

    return getBlock();
  }
}
