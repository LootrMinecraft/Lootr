package noobanidus.mods.lootr.mixins;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import noobanidus.mods.lootr.item.CrownItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO: When the crown gets implemented
@Mixin(PiglinAi.class)
public class MixinPiglinBrain {
  @Inject(method = "isWearingGold", at = @At("HEAD"), cancellable = true)
  private static void piglinIgnoreCrown(@NotNull LivingEntity livingEntity, @NotNull CallbackInfoReturnable<Boolean> cir) {
    if (livingEntity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof CrownItem) {
      cir.setReturnValue(true);
    }
  }
}
