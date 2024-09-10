package noobanidus.mods.lootr.common.mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PoiTypes.class)
public class MixinPoiTypes {
  @Inject(method = "forState", at = @At("RETURN"), cancellable = true)
  private static void LootrForState(BlockState state, CallbackInfoReturnable<Optional<Holder<PoiType>>> cir) {
    if (!LootrRegistry.isReady()) {
      return;
    }
    if (state.is(LootrRegistry.getBarrelBlock())) {
      cir.setReturnValue(Optional.of(BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolderOrThrow(PoiTypes.FISHERMAN)));
      cir.cancel();
    }
  }

  @Inject(method = "hasPoi", at = @At("RETURN"), cancellable = true)
  private static void LootrHasPoi(BlockState state, CallbackInfoReturnable<Boolean> cir) {
    if (!LootrRegistry.isReady()) {
      return;
    }
    if (state.is(LootrRegistry.getBarrelBlock())) {
      cir.setReturnValue(true);
      cir.cancel();
    }
  }
}
