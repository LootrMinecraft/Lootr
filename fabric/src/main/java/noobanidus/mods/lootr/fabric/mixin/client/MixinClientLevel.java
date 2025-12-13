package noobanidus.mods.lootr.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.fabric.network.to_server.PacketRequestUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// This is only needed because Fabric doesn't have the equivalent of NeoForge's
// fix #809 (https://github.com/neoforged/NeoForge/pull/809) which saves and then
// reloads block entity data.
@Mixin(ClientLevel.class)
public class MixinClientLevel {
  @WrapOperation(method="syncBlockState", at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/ClientLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
  private boolean lootr$onSetBlock(ClientLevel instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
    boolean result = original.call(instance, pos, state, flags);
    if (state.is(LootrTags.Blocks.CONTAINERS) && instance.getBlockEntity(pos) != null) {
      ClientPlayNetworking.send(new PacketRequestUpdate(new GlobalPos(instance.dimension(), pos)));
    }
    return result;
  }
}
