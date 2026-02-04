package noobanidus.mods.lootr.common.mixin.accessor;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkMap.class)
public interface AccessorMixinChunkMap {
  @Accessor("visibleChunkMap")
  Long2ObjectLinkedOpenHashMap<ChunkHolder> lootr$visibleChunkMap();

}
