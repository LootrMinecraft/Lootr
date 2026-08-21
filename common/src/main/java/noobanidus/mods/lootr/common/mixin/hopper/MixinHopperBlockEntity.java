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

    if (LootrAPI.resolveBlockEntity(level.getBlockEntity(pos)) instanceof ILootrBlockEntity) {
      return null;
    }

    return original.call(level, pos, state);
  }

  @WrapMethod(method = "getEntityContainer")
  private static Container lootr$preventHopperEntityInteraction(Level level, double x, double y, double z, Operation<Container> original) {
    Container result;
    try {
      result = original.call(level, x, y, z);
    } catch (ClassCastException castException) {
      LootrAPI.LOG.error("Non-Lootr Error: Another mod has caused the original `getEntityContainer` method of the `HopperBlockEntity` to fail with a ClassCastException.");
      LootrAPI.LOG.error("Although the crash has occurred in Lootr's code, this error is NOT caused by Lootr, but by another mod either altering `HopperBlockEntity::getEntityContainer` or `EntitySelector.CONTAINER_ENTITY_SELECTOR`, causing the list of entities returned to contain an entity or entities that are not also a `Container`. Lootr has prevented the error from causing block ticking to crash, but there may be further issues.");
      LootrAPI.LOG.error("This is the original exception:", castException);
      return null;
    }
    if (!(result instanceof Entity entity)) {
      return result;
    }
    if (entity.getType().is(LootrTags.Entity.CONTAINERS)) {
      return null;
    }
    if (LootrAPI.resolveEntity(entity) instanceof ILootrEntity) {
      return null;
    }

    return result;
  }
}
