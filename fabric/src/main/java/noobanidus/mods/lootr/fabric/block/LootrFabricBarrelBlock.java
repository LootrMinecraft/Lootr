package noobanidus.mods.lootr.fabric.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.block.LootrBarrelBlock;
import noobanidus.mods.lootr.fabric.block.entity.LootrFabricBarrelBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LootrFabricBarrelBlock extends LootrBarrelBlock {
  public LootrFabricBarrelBlock(Properties p_49046_) {
    super(p_49046_);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new LootrFabricBarrelBlockEntity(pos, state);
  }
}
