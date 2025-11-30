package noobanidus.mods.lootr.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetHandler.class)
public class MixinServerPlayNetHandler {
  @Redirect(method = "handleUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;mayInteract(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;)Z"))
  private boolean LootrAllowInteractSpawnProtection(ServerWorld level, PlayerEntity player, BlockPos position) {
    if (ConfigManager.BYPASS_SPAWN_PROTECTION.get()) {
      if (level.getBlockState(position).is(LootrTags.Blocks.INTERACT_WHITELIST_BLOCKS) && level.getServer()
          .isUnderSpawnProtection(level, position, player)) {
        return level.getWorldBorder().isWithinBounds(position);
      }
    }

    return level.mayInteract(player, position);
  }
}
