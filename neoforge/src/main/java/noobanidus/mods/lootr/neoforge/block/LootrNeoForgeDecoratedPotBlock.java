package noobanidus.mods.lootr.neoforge.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.block.LootrDecoratedPotBlock;
import noobanidus.mods.lootr.neoforge.block.entity.LootrNeoForgeDecoratedPotBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LootrNeoForgeDecoratedPotBlock extends LootrDecoratedPotBlock {
  public LootrNeoForgeDecoratedPotBlock(Properties properties) {
    super(properties);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new LootrNeoForgeDecoratedPotBlockEntity(blockPos, blockState);
  }
}
