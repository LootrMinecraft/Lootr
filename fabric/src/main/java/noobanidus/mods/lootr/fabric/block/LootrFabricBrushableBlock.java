package noobanidus.mods.lootr.fabric.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.block.LootrBrushableBlock;
import noobanidus.mods.lootr.fabric.block.entity.LootrFabricBrushableBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LootrFabricBrushableBlock extends LootrBrushableBlock {
  public LootrFabricBrushableBlock(Block pseudoReplacement, SoundEvent soundEvent, SoundEvent soundEvent2, Properties properties) {
    super(pseudoReplacement, soundEvent, soundEvent2, properties);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new LootrFabricBrushableBlockEntity(blockPos, blockState);
  }
}
