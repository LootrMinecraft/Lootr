package noobanidus.mods.lootr.common.integration.jade;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrTags;
import snownee.jade.api.*;

@WailaPlugin
public class LootrWailaPlugin implements IWailaPlugin {
  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
      // TODO: It can be null :/
      if (accessor instanceof BlockAccessor blockAccessor) {
        BlockState newState;
        if (blockAccessor.getBlockState().is(LootrTags.Blocks.SANDS)) {
          newState = Blocks.SAND.defaultBlockState();
        } else if (blockAccessor.getBlockState().is(LootrTags.Blocks.GRAVELS)) {
          newState = Blocks.GRAVEL.defaultBlockState();
        } else {
          return accessor;
        }

        return registration.blockAccessor().from(blockAccessor).blockState(newState).build();
      }
      return accessor;
    });
  }
}
