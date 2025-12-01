package noobanidus.mods.lootr.common.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
  @WrapOperation(method="handleUseItemOn", at=@At(value="INVOKE", target="Lnet/minecraft/server/level/ServerLevel;mayInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;)Z"))
  private boolean LootrAllowInteractSpawnProtection (ServerLevel instance, Player player, BlockPos blockPos, Operation<Boolean> original) {
    // TODO: This has the potential to break if `mayInteract` is ever extended to do more than just spawn protection checks.
    if (LootrAPI.shouldBypassSpawnProtection()) {
      if (instance.getBlockState(blockPos).is(LootrTags.Blocks.INTERACT_WHITELIST_BLOCKS) && instance.getServer()
          .isUnderSpawnProtection(instance, blockPos, player)) {
        return instance.getWorldBorder().isWithinBounds(blockPos);
      }
    }

    return original.call(instance, player, blockPos);
  }
}
