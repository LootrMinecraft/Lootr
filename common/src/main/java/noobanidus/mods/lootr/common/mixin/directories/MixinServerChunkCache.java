package noobanidus.mods.lootr.common.mixin.directories;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.FileUtil;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinDimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;

// An ugly solution to an ugly problem
@Mixin(ServerChunkCache.class)
public class MixinServerChunkCache {
  @Inject(method = "<init>", at = @At(value = "RETURN"))
  private void LootrServerChunkCacheInit(CallbackInfo ci) {
    ServerChunkCache cache = (ServerChunkCache) (Object) this;
    if (!cache.getLevel().dimension().equals(Level.OVERWORLD)) {
      return;
    }

    Path lootrDirectory = ((AccessorMixinDimensionDataStorage) cache.getDataStorage()).getDataFolder().resolve("lootr");
    Path tickingDirectory = lootrDirectory.resolve("ticking");
    Path refreshDirectory = tickingDirectory.resolve("refresh");
    Path decayedDirectory = tickingDirectory.resolve("delaced");
    try {
      FileUtil.createDirectoriesSafe(lootrDirectory);
      FileUtil.createDirectoriesSafe(tickingDirectory);
      FileUtil.createDirectoriesSafe(refreshDirectory);
      FileUtil.createDirectoriesSafe(decayedDirectory);

      for (String digit : LootrAPI._lootr$digits) {
        Path subPath1 = lootrDirectory.resolve(digit);

        FileUtil.createDirectoriesSafe(subPath1);

        for (String digit2 : LootrAPI._lootr$digits) {
          Path subPath2 = subPath1.resolve(digit + digit2);

          FileUtil.createDirectoriesSafe(subPath2);

        }
      }

    } catch (IOException e) {
      LootrAPI.LOG.error("Failed to create initial Lootr data directory: {}", lootrDirectory);
    }
  }
}
