package noobanidus.mods.lootr.common.mixin.team;

import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import noobanidus.mods.lootr.common.impl.team.MinecraftDefaultTeamResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Scoreboard.class)
public class MixinScoreboard {
  @Inject(method="onTeamAdded", at=@At("HEAD"))
  private void lootr$onTeamAdded (PlayerTeam playerTeam, CallbackInfo ci) {
    MinecraftDefaultTeamResolver.resetCache();
  }

  @Inject(method="onTeamRemoved", at=@At("HEAD"))
  private void lootr$onTeamRemoved (PlayerTeam playerTeam, CallbackInfo ci) {
    MinecraftDefaultTeamResolver.resetCache();
  }
}
