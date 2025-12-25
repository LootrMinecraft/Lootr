package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface AccessorMixinMinecraftServer {
  @Accessor("storageSource")
  LevelStorageSource.LevelStorageAccess Lootr$getStorageSource();
}
