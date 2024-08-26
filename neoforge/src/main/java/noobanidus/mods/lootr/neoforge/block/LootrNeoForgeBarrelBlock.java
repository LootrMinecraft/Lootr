package noobanidus.mods.lootr.neoforge.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.block.LootrBarrelBlock;
import noobanidus.mods.lootr.neoforge.block.entity.LootrNeoForgeBarrelBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LootrNeoForgeBarrelBlock extends LootrBarrelBlock {
  public LootrNeoForgeBarrelBlock(Properties p_49046_) {
    super(p_49046_);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new LootrNeoForgeBarrelBlockEntity(pos, state);
  }
}
