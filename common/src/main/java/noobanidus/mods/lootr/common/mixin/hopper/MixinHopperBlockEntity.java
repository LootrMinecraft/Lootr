package noobanidus.mods.lootr.common.mixin.hopper;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
  @WrapMethod(method = "getBlockContainer")
  private static Container lootr$preventHopperBlockInteraction(Level level, BlockPos pos, BlockState state, Operation<Container> original) {
    if (state.is(LootrTags.Blocks.CONTAINERS)) {
      return null;
    }

    if (LootrAPI.wrapBlockEntity(level.getBlockEntity(pos)) instanceof ILootrBlockEntity) {
      return null;
    }

    return original.call(level, pos, state);
  }

  @WrapMethod(method="getEntityContainer")
  private static Container lootr$preventHopperEntityInteraction(Level level, double x, double y, double z, Operation<Container> original) {
    var result = original.call(level, x, y, z);
    if (!(result instanceof Entity entity)) {
      return result;
    }
    if (entity.is(LootrTags.Entity.CONTAINERS)) {
      return null;
    }
    if (LootrAPI.wrapEntity(entity) instanceof ILootrEntity) {
      return null;
    }

    return result;
  }
}
