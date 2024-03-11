package noobanidus.mods.lootr.mixins;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PoiType.class)
public class MixinPoiType {
  @Unique
  private boolean fishermanCheck;
  @Unique
  private boolean isFisherman;
  @Inject(method = "is", at = @At(value = "RETURN"), cancellable = true)
  private void LootrGetBlockStates(BlockState state, CallbackInfoReturnable<Boolean> cir) {
    PoiType thisPoi = (PoiType) (Object) this;
    if (!fishermanCheck) {
      fishermanCheck = true;
      isFisherman = ForgeRegistries.POI_TYPES.getKey(thisPoi).equals(PoiTypes.FISHERMAN);
    }
    if (isFisherman) {
      if (state.is(ModBlocks.BARREL.get())) {
        cir.setReturnValue(true);
        cir.cancel();
      }
    }
  }
}
