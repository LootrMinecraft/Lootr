package noobanidus.mods.lootr.common.mixin.structure_saving;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueOutput;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureTemplate.class)
public class MixinStructureTemplate {
  @WrapOperation(method = "fillFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveWithId(Lnet/minecraft/world/level/storage/ValueOutput;)V"))
  private void LootrInjectStructureSavingStart(BlockEntity instance, ValueOutput output, Operation<Void> original) {
    LootrAPI.shouldDiscardIdAndOpeners = true;
    original.call(instance, output);
    LootrAPI.shouldDiscardIdAndOpeners = false;
  }
}
