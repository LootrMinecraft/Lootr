package noobanidus.mods.lootr.neoforge.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.block.LootrBrushableBlock;
import noobanidus.mods.lootr.common.block.entity.LootrBrushableBlockEntity;
import noobanidus.mods.lootr.neoforge.block.entity.LootrNeoForgeBrushableBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LootrNeoForgeBrushableBlock extends LootrBrushableBlock {
  public LootrNeoForgeBrushableBlock(SoundEvent soundEvent, SoundEvent soundEvent2, Properties properties) {
    super(soundEvent, soundEvent2, properties);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new LootrNeoForgeBrushableBlockEntity(blockPos, blockState);
  }
}
