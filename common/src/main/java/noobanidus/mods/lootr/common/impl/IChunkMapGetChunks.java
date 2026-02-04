package noobanidus.mods.lootr.common.impl;

import net.minecraft.server.level.ChunkHolder;

public interface IChunkMapGetChunks {
  Iterable<ChunkHolder> lootr$getChunks();
}
