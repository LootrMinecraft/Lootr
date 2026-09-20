package noobanidus.mods.lootr.common.mixin.team;

import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.impl.team.MinecraftDefaultTeamResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scoreboard.class)
public class MixinScoreboard {
  @Inject(method="onTeamAdded", at=@At("HEAD"))
  private void lootr$onTeamAdded (PlayerTeam playerTeam, CallbackInfo ci) {
    MinecraftDefaultTeamResolver.resetCache();
    if (LootrAPI.isTeamLoot()) {
      PlatformAPI.syncAfterTeamChange(playerTeam);
    }
  }

  @Inject(method="onTeamRemoved", at=@At("HEAD"))
  private void lootr$onTeamRemoved (PlayerTeam playerTeam, CallbackInfo ci) {
    MinecraftDefaultTeamResolver.resetCache();
    if (LootrAPI.isTeamLoot()) {
      PlatformAPI.syncAfterTeamChange(playerTeam);
    }
  }

  @Inject(method="addPlayerToTeam", at=@At("RETURN"))
  private void lootr$onPlayerAddedToTeam (String playerName, PlayerTeam team, CallbackInfoReturnable<Boolean> cir) {
    MinecraftDefaultTeamResolver.resetCache();
    if (LootrAPI.isTeamLoot()) {
      PlatformAPI.syncAfterTeamChange(team);
    }
  }

  @Inject(method="removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/world/scores/PlayerTeam;)V", at=@At("RETURN"))
  private void lootr$onPlayerRemovedFromTeam (String username, PlayerTeam playerTeam, CallbackInfo ci) {
    MinecraftDefaultTeamResolver.resetCache();
    if (LootrAPI.isTeamLoot()) {
      PlatformAPI.syncAfterTeamChange(playerTeam);
      PlatformAPI.syncAfterTeamChange(username);
    }
  }
}
