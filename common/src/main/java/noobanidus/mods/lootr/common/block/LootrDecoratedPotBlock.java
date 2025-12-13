package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LootrDecoratedPotBlock extends DecoratedPotBlock {
  public LootrDecoratedPotBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
    return super.playerWillDestroy(level, blockPos, blockState, player);
  }
}
