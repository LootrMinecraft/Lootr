package noobanidus.mods.lootr.common.mixin.redstone;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrInfo;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class MixinAbstractContainerMenu {
  // This prevents inventory access to Lootr Minecarts etc, but doesn't interfere with Lootr Item Frames. In theory.
  @Inject(method="getRedstoneSignalFromContainer", at=@At(value="HEAD"), cancellable = true)
  private static void lootr$getRedstoneSignalFromContainer(Container container, CallbackInfoReturnable<Integer> cir) {
    if (container instanceof ILootrInfo && !(container instanceof LootrItemFrame)) {
      if (LootrAPI.shouldPowerComparators()) {
        cir.setReturnValue(1);
      } else {
        cir.setReturnValue(0);
      }
    }
  }
}
