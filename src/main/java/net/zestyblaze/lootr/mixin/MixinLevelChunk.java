package net.zestyblaze.lootr.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.blocks.entities.TileTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class MixinLevelChunk {
  @Inject(method="addAndRegisterBlockEntity", at=@At(target="Lnet/minecraft/world/level/chunk/LevelChunk;updateBlockEntityTicker(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", value="INVOKE", shift= At.Shift.AFTER))
  private void lootrAddAndRegisterBlockEntity (BlockEntity entity, CallbackInfo cir) {
    if (entity instanceof RandomizableContainerBlockEntity && !(entity instanceof ILootBlockEntity)) {
      LevelChunk levelChunk = (LevelChunk) (Object) this;
      if (!levelChunk.getLevel().isClientSide()) {
        TileTicker.addEntry(levelChunk.getLevel().dimension(), entity.getBlockPos());
      }
    }
  }
}
