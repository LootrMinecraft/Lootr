package noobanidus.mods.lootr.fabric.mixins;

import net.minecraft.server.ReloadableServerResources;
import noobanidus.mods.lootr.common.debug.TagChecker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class MixinReloadableServerResources {
  @Inject(method= "updateRegistryTags()V", at=@At("RETURN"))
  public void LootrCheckTags (CallbackInfo ci) {
    TagChecker.checkTags();
  }
}
