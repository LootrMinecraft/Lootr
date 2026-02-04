package noobanidus.mods.lootr.common.mixin.accessor;

import com.google.common.collect.Iterables;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import noobanidus.mods.lootr.common.impl.IChunkMapGetChunks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkMap.class)
public class MixinChunkMap implements IChunkMapGetChunks {
  @Unique
  public Iterable<ChunkHolder> lootr$getChunks() {
    return Iterables.unmodifiableIterable(((AccessorMixinChunkMap) this).lootr$visibleChunkMap().values());
  }
}
