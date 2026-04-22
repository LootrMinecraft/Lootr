package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@Mixin(DimensionDataStorage.class)
public interface AccessorMixinDimensionDataStorage {
  @Accessor("cache")
  Map<SavedDataType<?>, Optional<SavedData>> lootr$getCache();

  @Accessor("dataFolder")
  Path lootr$getDataFolder();
}
