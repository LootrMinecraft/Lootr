package noobanidus.mods.lootr.common.mixins;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import noobanidus.mods.lootr.common.data.LootrSavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(DimensionDataStorage.class)
public class MixinDimensionDataStorage {
  @Shadow
  @Final
  private Map<String, SavedData> cache;

  @Inject(method="save",at=@At("TAIL"))
  private void save (CallbackInfo ci) {
    Set<String> toUnload = new HashSet<>();
    for (Map.Entry<String, SavedData> entry : cache.entrySet()) {
      if (entry.getValue() instanceof LootrSavedData lootrSavedData) {
        if (lootrSavedData.isDirty()) {
          continue;
        }

        if (lootrSavedData.shouldUnload()) {
          toUnload.add(entry.getKey());
        }
      }
    }
    for (String toRemove : toUnload) {
      cache.remove(toRemove);
    }

  }
}
