package noobanidus.mods.lootr.mixins;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.entities.TileTicker;
import noobanidus.mods.lootr.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class MixinLevelChunk {
  @Inject(method = "updateBlockEntityTicker", at = @At(value = "HEAD"))
  private void lootrUpdateBlockEntityTicker(BlockEntity entity, CallbackInfo cir) {
    if (ConfigManager.get().conversion.disable) {
      return;
    }
    if (entity instanceof RandomizableContainerBlockEntity && !(entity instanceof ILootBlockEntity)) {
      LevelChunk level = (LevelChunk) (Object) this;
      if (level.getLevel().isClientSide()) {
        return;
      }
      if (ConfigManager.get().conversion.world_border && !level.getLevel().getWorldBorder().isWithinBounds(entity.getBlockPos())) {
        return;
      }
      TileTicker.addEntry(level.getLevel(), entity.getBlockPos());
    }
  }
}
