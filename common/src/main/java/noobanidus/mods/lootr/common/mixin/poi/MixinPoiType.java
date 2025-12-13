package noobanidus.mods.lootr.common.mixin.poi;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PoiType.class)
public class MixinPoiType {
  @Unique
  private boolean lootr$fishermanCheck;
  @Unique
  private boolean lootr$isFisherman;

  @Inject(method = "is", at = @At(value = "RETURN"), cancellable = true)
  private void LootrGetBlockStates(BlockState state, CallbackInfoReturnable<Boolean> cir) {
    if (!LootrRegistry.isReady()) {
      return;
    }
    PoiType thisPoi = (PoiType) (Object) this;
    if (!lootr$fishermanCheck) {
      lootr$fishermanCheck = true;
      lootr$isFisherman = PoiTypes.FISHERMAN.location().equals(BuiltInRegistries.POINT_OF_INTEREST_TYPE.getKey(thisPoi));
    }
    if (lootr$isFisherman) {
      if (state.is(LootrRegistry.getBarrelBlock())) {
        cir.setReturnValue(true);
        cir.cancel();
      }
    }
  }
}
