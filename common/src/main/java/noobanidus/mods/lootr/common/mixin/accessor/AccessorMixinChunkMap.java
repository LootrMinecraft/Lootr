package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface AccessorMixinChunkMap {
  // TODO
  @Invoker("getChunks")
  Iterable<ChunkHolder> lootr$getChunks();
}
