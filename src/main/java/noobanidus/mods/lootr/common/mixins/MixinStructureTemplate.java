package noobanidus.mods.lootr.common.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureTemplate.class)
public class MixinStructureTemplate {
  @Inject(method = "fillFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveWithId(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;", shift = At.Shift.BEFORE))
  private void LootrInjectStructureSavingStart(Level p_163803_, BlockPos p_163804_, Vec3i p_163805_, boolean p_163806_, Block p_163807_, CallbackInfo ci) {
    LootrAPI.shouldDiscardIdAndOpeners = true;
  }

  @Inject(method = "fillFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveWithId(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;", shift = At.Shift.AFTER))
  private void LootrInjectStructureSavingStop(Level p_163803_, BlockPos p_163804_, Vec3i p_163805_, boolean p_163806_, Block p_163807_, CallbackInfo ci) {
    LootrAPI.shouldDiscardIdAndOpeners = false;
  }
}
