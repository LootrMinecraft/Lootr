package noobanidus.mods.lootr.common.mixin.sync;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public class MixinClientLevel {
  @WrapOperation(method = "syncBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
  private boolean lootr$onSetBlock(ClientLevel instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
    boolean result = original.call(instance, pos, state, flags);
    if (state.is(LootrTags.Blocks.CONTAINERS) && instance.getBlockEntity(pos) != null) {
      PlatformAPI.performRequestSync(new GlobalPos(instance.dimension(), pos));
    }
    return result;
  }
}
