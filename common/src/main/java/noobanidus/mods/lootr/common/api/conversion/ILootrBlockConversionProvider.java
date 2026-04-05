package noobanidus.mods.lootr.common.api.conversion;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Provides rules for determining replacement BlockStates during Lootr conversion.
 * <br />
 * Providers are loaded via services. Specifically, the class implementing this
 * converter should be listed (fully qualified name) in a file located at:
 * META-INF/services/noobanidus.mods.lootr.common.api.replacement.ILootrBlockReplacementProvider
 * <br />
 * The default implementations can be found in noobanidus.mods.lootr.common.impl.replacement.
 * <br />
 * This should be used when you wish to tell Lootr what block to replace another block with.
 * <br />
 * Due to the overlapping nature of tags, multiple providers may match a given block.
 * At this point, priority determines the outcome.
 */
public interface ILootrBlockConversionProvider extends Function<Block, @Nullable Block> {
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

  default BlockState copyTypeSpecificProperties (BlockState from, BlockState to) {
    return to;
  }
}
