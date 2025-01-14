package noobanidus.mods.lootr.common.config;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import noobanidus.mods.lootr.common.api.IReplacementProvider;
import org.jetbrains.annotations.Nullable;

public class DefaultStateMapper implements IReplacementProvider.StateMapper {
  public static final DefaultStateMapper INSTANCE = new DefaultStateMapper();

  @Override
  public @Nullable BlockState map(BlockState newState, BlockState originalState, BlockEntity blockEntity) {
    BlockState state = newState;
    for (Property<?> prop : originalState.getProperties()) {
      if (state.hasProperty(prop)) {
        state = Replacements.safeReplace(state, originalState, prop);
      }
    }
    return state;
  }
}
