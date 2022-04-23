package noobanidus.mods.lootr.mixins;


import net.minecraft.world.storage.SaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(SaveHandler.class)
public class MixinSaveHandler {
    @Inject(method = "getMapFileFromName", at = @At("TAIL"))
    private void makeParentDirs(String mapName, CallbackInfoReturnable<File> cir) {
        File file = cir.getReturnValue();
        if(file != null) {
            file.getParentFile().mkdirs();
        }
    }
}
