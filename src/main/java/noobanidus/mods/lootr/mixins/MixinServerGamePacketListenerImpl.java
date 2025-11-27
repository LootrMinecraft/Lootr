package noobanidus.mods.lootr.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
  @Redirect(method = "handleUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;mayInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;)Z"))
  private boolean LootrAllowInteractSpawnProtection(ServerLevel level, Player player, BlockPos position) {
    // TODO: This has the potential to break if `mayInteract` is ever extended to do more than just spawn protection checks.
    if (ConfigManager.BYPASS_SPAWN_PROTECTION.get()) {
      if (level.getBlockState(position).is(LootrTags.Blocks.INTERACT_WHITELIST_BLOCKS) && level.getServer()
          .isUnderSpawnProtection(level, position, player)) {
        return level.getWorldBorder().isWithinBounds(position);
      }
    }

    return level.mayInteract(player, position);
  }
}
