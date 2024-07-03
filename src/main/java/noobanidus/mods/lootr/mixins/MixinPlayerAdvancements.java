package noobanidus.mods.lootr.mixins;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import noobanidus.mods.lootr.init.ModAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class MixinPlayerAdvancements {
  @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"))
  private void lootrAward(AdvancementHolder advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir) {
    PlayerAdvancements playerAdvancements = (PlayerAdvancements) (Object) this;
    if (!playerAdvancements.player.level().isClientSide()) {
      ModAdvancements.ADVANCEMENT_PREDICATE.trigger(playerAdvancements.player, advancement.id());
    }
  }
}
