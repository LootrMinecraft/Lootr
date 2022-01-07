package noobanidus.mods.lootr.util;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import javax.annotation.Nullable;
import java.util.Map;

public class StructureUtil {
  @Nullable
  // NB: Should only be called when the level is definitely generated
  public static StructureFeature<?> featureFor (ServerLevel level, BlockPos pos) {
    ChunkPos cPos = new ChunkPos(pos);
    ChunkAccess chunk = level.getChunk(cPos.x, cPos.z, ChunkStatus.STRUCTURE_REFERENCES);
    StructureFeatureManager manager = level.structureFeatureManager();
    Map<StructureFeature<?>, LongSet> references = chunk.getAllReferences();
    for (Map.Entry<StructureFeature<?>, LongSet> entry : references.entrySet()) {
      for (long i : entry.getValue()) {
        SectionPos sec = SectionPos.of(new ChunkPos(i), level.getMinSection());
        StructureStart<?> start = manager.getStartForFeature(sec, entry.getKey(), level.getChunk(sec.x(), sec.z(), ChunkStatus.STRUCTURE_STARTS));
        if (start != null && start.isValid() && start.getBoundingBox().isInside(pos)) {
          return entry.getKey();
        }
      }
    }

    return null;
  }
}
