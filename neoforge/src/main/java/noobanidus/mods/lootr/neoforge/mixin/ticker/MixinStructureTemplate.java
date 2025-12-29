package noobanidus.mods.lootr.neoforge.mixin.ticker;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import noobanidus.mods.lootr.common.impl.ItemFrameHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureTemplate.class)
public class MixinStructureTemplate {
  @WrapOperation(method = "lambda$addEntitiesToWorld$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
  private static void lootr$AddEntitiesToWorldInject(ServerLevelAccessor level, Entity entity, Operation<Void> original) {
    ItemFrameHandler.maybeReplaceItemFrames(level, entity, original);
  }
}
