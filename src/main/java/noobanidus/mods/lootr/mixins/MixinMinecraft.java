package noobanidus.mods.lootr.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.api.LootrAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
  @Inject(method="addCustomNbtData", at=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/entity/BlockEntity;saveWithFullMetadata()Lnet/minecraft/nbt/CompoundTag;", shift=At.Shift.BEFORE))
  private void LootrInjectStructureSavingStart(ItemStack p_263370_, BlockEntity p_263368_, CallbackInfo ci) {
    LootrAPI.shouldDiscardIdAndOpeners = true;
  }
  @Inject(method="addCustomNbtData", at=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/entity/BlockEntity;saveWithFullMetadata()Lnet/minecraft/nbt/CompoundTag;", shift=At.Shift.AFTER))
  private void LootrInjectStructureSavingStop (ItemStack p_263370_, BlockEntity p_263368_, CallbackInfo ci) {
    LootrAPI.shouldDiscardIdAndOpeners = false;
  }
}
