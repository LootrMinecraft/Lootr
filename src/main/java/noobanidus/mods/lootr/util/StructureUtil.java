package noobanidus.mods.lootr.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StructureUtil {
  private static final BoundingBox DESERT_PYRAMID_BOX = new BoundingBox(-2, -20, -2, 3, 2, 3);
  private static final ResourceLocation DESERT_PYRAMID = new ResourceLocation("minecraft", "desert_pyramid");
  private static final ResourceLocation JUNGLE_PYRAMID = new ResourceLocation("minecraft", "jungle_pyramid");

/*  @Nullable
  public static StructureFeature<?> featureFor(ServerLevel level, BlockPos pos) {
    ChunkPos cPos = new ChunkPos(pos);
    ChunkAccess chunk = level.getChunk(cPos.x, cPos.z, ChunkStatus.STRUCTURE_REFERENCES);
    StructureFeatureManager manager = level.structureFeatureManager();
    Map<ConfiguredStructureFeature<?, ?>, LongSet> references = chunk.getAllReferences();
    for (Map.Entry<ConfiguredStructureFeature<?, ?>, LongSet> entry : references.entrySet()) {
      for (long i : entry.getValue()) {
        SectionPos sec = SectionPos.of(new ChunkPos(i), level.getMinSection());
        StructureStart start = manager.getStartForFeature(sec, entry.getKey(), level.getChunk(sec.x(), sec.z(), ChunkStatus.STRUCTURE_STARTS));
        if (start != null && start.isValid()) {
          BoundingBox box = start.getBoundingBox();
          if (box.getCenter().distSqr(pos) > 15 * 15) {
            box = start.getFeature().adjustBoundingBox(StructurePiece.createBoundingBox(start.getPieces().stream()));
          }
          if (JUNGLE_PYRAMID.equals(entry.getKey().feature.getRegistryName())) {
            box = new BoundingBox(box.minX(), box.minY() - 2, box.minZ(), box.maxX(), box.maxY(), box.maxZ());
          }
          if (box.isInside(pos)) {
            return entry.getKey().feature;
          }
          if (DESERT_PYRAMID.equals(entry.getKey().feature.getRegistryName())) {
            BlockPos corner = box.getCenter();
            BoundingBox additional = DESERT_PYRAMID_BOX.moved(corner.getX(), corner.getY(), corner.getZ());
            if (additional.isInside(pos)) {
              return entry.getKey().feature;
            }
          }
        }
      }
    }

    return null;
  }*/
}
