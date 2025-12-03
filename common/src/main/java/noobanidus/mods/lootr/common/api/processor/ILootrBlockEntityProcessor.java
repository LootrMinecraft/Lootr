package noobanidus.mods.lootr.common.api.processor;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface ILootrBlockEntityProcessor {
  non-sealed interface Pre extends ILootrProcessor.Pre<BlockEntity> {

  }

  non-sealed interface Post extends ILootrProcessor.Post<BlockEntity> {

  }
}
