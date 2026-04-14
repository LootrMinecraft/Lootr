package noobanidus.mods.lootr.common.mixin.hopper;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
  // Prevents hoppers from hijacking container entities, however there's an issue
  // because this doesn't fix EntitySelector.CONTAINER_ENTITY_SELECTOR
  @WrapOperation(method="getContainerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;DDD)Lnet/minecraft/world/Container;", at=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getEntityContainer(Lnet/minecraft/world/level/Level;DDD)Lnet/minecraft/world/Container;"))
  private static Container lootr$getEntityContainer(Level level, double x, double y, double z, Operation<Container> original) {
    var result = original.call(level, x, y, z);
    if (result instanceof ILootrEntity) {
      return null;
    }

    return result;
  }
}
