package noobanidus.mods.lootr.common.mixin.hopper;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntitySelector.class)
public class MixinEntitySelector {
  // I'm not sure if this is really necessary
  // TODO; Still not sure if this is necessary
  @WrapOperation(method = {"lambda$static$2", "method_5914"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isAlive()Z"))
  private static boolean lootr$checkContainerEntitySelector(Entity instance, Operation<Boolean> original) {
    if (instance.getType().is(LootrTags.Entity.CONTAINERS)) {
      return false;
    }
    return original.call(instance);
  }
}
