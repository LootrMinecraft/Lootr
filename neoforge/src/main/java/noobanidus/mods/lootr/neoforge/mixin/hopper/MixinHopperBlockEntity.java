package noobanidus.mods.lootr.neoforge.mixin.hopper;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.neoforged.neoforge.transfer.item.ContainerOrHandler;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
  @WrapMethod(method="getContainerOrHandlerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lnet/neoforged/neoforge/transfer/item/ContainerOrHandler;")
  private static ContainerOrHandler lootr$preventHopperCartInjection (Level level, BlockPos pos, Direction side, Operation<ContainerOrHandler> original) {
    ContainerOrHandler result = original.call(level, pos, side);
    if (!result.isEmpty() && result.container() != null && result.container() instanceof Entity entity && LootrAPI.wrapEntity(entity) instanceof ILootrEntity) {
      return ContainerOrHandler.EMPTY;
    }

    return result;
  }
}
