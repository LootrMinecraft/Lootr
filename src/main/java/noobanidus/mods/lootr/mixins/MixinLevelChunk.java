package noobanidus.mods.lootr.mixins;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.entities.TileTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class MixinLevelChunk {
  @Inject(method="addAndRegisterBlockEntity", at=@At(value="RETURN"))
  private void lootrAddAndRegisterBlockEntity (BlockEntity entity, CallbackInfo cir) {
    if (entity instanceof RandomizableContainerBlockEntity && !(entity instanceof ILootBlockEntity)) {
      LevelChunk levelChunk = (LevelChunk) (Object) this;
      if (!levelChunk.getLevel().isClientSide() && levelChunk.getLevel().getWorldBorder().isWithinBounds(entity.getBlockPos())) {
        TileTicker.addEntry(levelChunk.getLevel(), entity.getBlockPos());
      }
    }
  }
}
