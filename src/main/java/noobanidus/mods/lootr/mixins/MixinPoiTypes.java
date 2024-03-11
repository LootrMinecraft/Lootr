package noobanidus.mods.lootr.mixins;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PoiTypes.class)
public class MixinPoiTypes {
  @Inject(method = "forState", at = @At("RETURN"), cancellable = true)
  private static void LootrForState(BlockState state, CallbackInfoReturnable<Optional<Holder<PoiType>>> cir) {
    if (state.is(ModBlocks.BARREL.get())) {
      cir.setReturnValue(ForgeRegistries.POI_TYPES.getHolder(PoiTypes.FISHERMAN));
      cir.cancel();
    }
  }
}
