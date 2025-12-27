package noobanidus.mods.lootr.common.mixin.falling;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// This is necessary otherwise previously opened falling brushable blocks
// will revert to their "open" status when they land.
@Mixin(FallingBlockEntity.class)
public class MixinFallingBlockEntity {
  @WrapOperation(method="tick", at=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/entity/BlockEntity;setChanged()V"))
  private void lootr$onSetChanged(BlockEntity instance, Operation<Void> original) {
    original.call(instance);
    ILootrBlockEntity resolved = null;
    if (LootrAPI.resolveBlockEntity(instance) instanceof ILootrBlockEntity resolved2) {
      resolved = resolved2;
    } else if (instance instanceof ILootrBlockEntity resolved2) {
      resolved = resolved2;
    }
    if (resolved != null) {
      resolved.performUpdate();
    }
  }
}
