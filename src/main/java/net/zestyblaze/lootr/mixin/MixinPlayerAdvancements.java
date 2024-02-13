package net.zestyblaze.lootr.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.zestyblaze.lootr.registry.LootrAdvancementsInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class MixinPlayerAdvancements {
  @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"))
  private void lootrAward(AdvancementHolder advancementHolder, String string, CallbackInfoReturnable<Boolean> cir) {
    PlayerAdvancements playerAdvancements = (PlayerAdvancements) (Object) this;
    if (!playerAdvancements.player.level().isClientSide()) {
      LootrAdvancementsInit.ADVANCEMENT_PREDICATE.trigger(playerAdvancements.player, advancementHolder.id());
    }
  }
}
