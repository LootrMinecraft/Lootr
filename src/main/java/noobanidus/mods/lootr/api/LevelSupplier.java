package noobanidus.mods.lootr.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class LevelSupplier implements Supplier<Level> {
  private WeakReference<Level> level;
  private final ResourceKey<Level> levelKey;

  public LevelSupplier(Level level) {
    this.level = new WeakReference<>(level);
    this.levelKey = level.dimension();
  }

  public LevelSupplier(ResourceKey<Level> levelKey) {
    this.level = null;
    this.levelKey = levelKey;
  }

  @Override
  public Level get() {
    if (this.level != null) {
      Level level = this.level.get();
      if (level != null) {
        return level;
      } else {
        this.level = null;
      }
    }
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      return null;
    }

    this.level = new WeakReference<>(server.getLevel(this.levelKey));
    return this.level.get();
  }
}
