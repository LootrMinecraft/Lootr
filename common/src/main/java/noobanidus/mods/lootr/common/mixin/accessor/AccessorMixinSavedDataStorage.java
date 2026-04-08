package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@Mixin(SavedDataStorage.class)
public interface AccessorMixinSavedDataStorage {
  @Accessor
  Map<SavedDataType<?>, Optional<SavedData>> getCache();

  @Accessor
  Path getDataFolder();
}
