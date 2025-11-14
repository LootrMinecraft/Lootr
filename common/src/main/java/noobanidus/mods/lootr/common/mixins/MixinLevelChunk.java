package noobanidus.mods.lootr.common.mixins;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(LevelChunk.class)
public class MixinLevelChunk {
  @Inject(method = "registerAllBlockEntitiesAfterLevelLoad", at = @At(value = "HEAD"))
  private void LootrRegisterAllBlockEntitiesAfterLevelLoad(CallbackInfo ci) {
    if (LootrAPI.isDisabled()) {
      return;
    }
    LevelChunk level = (LevelChunk) (Object) this;
    Collection<BlockEntity> blockEntities = level.getBlockEntities().values();
    BlockEntityTicker.addChunkEntities(blockEntities, level.getLevel(), level.getPos());
  }
}