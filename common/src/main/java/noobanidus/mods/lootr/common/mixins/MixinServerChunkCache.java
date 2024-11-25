package noobanidus.mods.lootr.common.mixins;

import net.minecraft.FileUtil;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;

// An ugly solution
@Mixin(ServerChunkCache.class)
public class MixinServerChunkCache {
  @Inject(method = "<init>", at = @At(value = "RETURN"))
  private void LootrServerChunkCacheInit(CallbackInfo ci) {
    ServerChunkCache cache = (ServerChunkCache) (Object) this;
    if (!cache.getLevel().dimension().equals(Level.OVERWORLD)) {
      return;
    }

    Path lootrDirectory = ((AccessorDimensionDataStorage) cache.getDataStorage()).getDataFolder().resolve("lootr");
    try {
      FileUtil.createDirectoriesSafe(lootrDirectory);

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
