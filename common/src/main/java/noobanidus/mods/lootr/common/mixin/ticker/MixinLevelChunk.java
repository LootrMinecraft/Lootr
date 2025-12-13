package noobanidus.mods.lootr.common.mixin.ticker;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class MixinLevelChunk {
  @Inject(method = "updateBlockEntityTicker", at = @At(value = "HEAD"))
  private void LootrUpdateBlockEntityTicker(BlockEntity entity, CallbackInfo cir) {
    if (LootrAPI.isDisabled()) {
      return;
    }
    LevelChunk level = (LevelChunk) (Object) this;
    BlockEntityTicker.addEntity(entity, level.getLevel(), level.getPos());
  }
}