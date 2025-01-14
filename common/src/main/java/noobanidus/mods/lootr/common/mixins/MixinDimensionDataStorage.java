package noobanidus.mods.lootr.common.mixins;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DimensionDataStorage.class)
public interface MixinDimensionDataStorage {
  @Accessor
  Map<String, SavedData> getCache();
}
