package noobanidus.mods.lootr.common.mixin.hopper;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class MixinAbstractContainerMenu {
  @Inject(method = "getRedstoneSignalFromContainer", at = @At("HEAD"), cancellable = true)
  private static void lootr$fixEntityRedstoneSignal(Container container, CallbackInfoReturnable<Integer> cir) {
    if (container instanceof Entity entity && entity.is(LootrTags.Entity.CONTAINERS)) {
      cir.setReturnValue(LootrAPI.shouldPowerComparators() ? 1 : 0);
      cir.cancel();
    }
  }
}
