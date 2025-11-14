package noobanidus.mods.lootr.common.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.DataToCopy;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

import java.util.Iterator;
import java.util.Set;

public class BlockEntityTicker {
  private final static Set<Entry> blockEntityEntries = new ObjectOpenHashSet<>();
  private final static Set<Entry> pendingEntries = new ObjectOpenHashSet<>();

  public static void addEntry(RandomizableContainerBlockEntity incoming, Level level, BlockPos position) {
    if (LootrAPI.isDisabled()) {
      return;
    }

    // By default block entities outside of the world border are
    // not converted. When the world border changes, you will
    // need to restart the server.
    if (!LootrAPI.isWorldBorderSafe(level, position)) {
      return;
    }

    if (LootrAPI.getServer() == null) {
      return;
    }

    ResourceKey<Level> dimension = level.dimension();
    if (LootrAPI.isDimensionBlocked(dimension)) {
      return;
    }

    ChunkPos chunkPos = new ChunkPos(position);

    Set<ChunkPos> chunks = new ObjectOpenHashSet<>();
    chunks.add(chunkPos);

    int oX = chunkPos.x;
    int oZ = chunkPos.z;
    chunks.add(chunkPos);

    for (int x = -2; x <= 2; x++) {
      for (int z = -2; z <= 2; z++) {
        ChunkPos newPos = new ChunkPos(oX + x, oZ + z);
        // This has the potential to force-load chunks on the main thread
        // by ignoring the loading state of chunks outside the world border.
        if (!LootrAPI.isWorldBorderSafe(level, newPos)) {
          continue;
        }

        chunks.add(newPos);
      }
    }

    if (incoming.getLootTable() != null && LootrAPI.isLootTableBlacklisted(incoming.getLootTable())) {
      return;
    }

    Entry newEntry = new Entry(dimension, position, chunks, LootrAPI.getCurrentTicks());
    synchronized (pendingEntries) {
      pendingEntries.add(newEntry);
    }
  }

  public static void onServerTick() {
    if (LootrAPI.isDisabled()) {
      return;
    }
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      LootrAPI.LOG.error("MinecraftServer was null during ServerTickEvent!");
      return;
    }
    Iterator<Entry> iterator = blockEntityEntries.iterator();
    while (iterator.hasNext()) {
      Entry entry = iterator.next();
      ServerLevel level = server.getLevel(entry.getDimension());
      if (level == null || LootrAPI.hasExpired(entry.age(server)) || (!LootrAPI.isWorldBorderSafe(level, entry.getPosition()))) {
        iterator.remove();
        continue;
      }

      if (!level.getChunkSource().hasChunk(entry.getPosition().getX() >> 4, entry.getPosition().getZ() >> 4)) {
        continue;
      }

      boolean skip = false;
      for (ChunkPos chunkPos : entry.getChunkPositions()) {
        if (!level.getChunkSource().hasChunk(chunkPos.x, chunkPos.z)) {
          skip = true;
          break;
        }
      }
      if (skip) {
        continue;
      }

      if (LootrAPI.anyUnloadedChunks(entry.getDimension(), entry.getChunkPositions())) {
        continue;
      }

      if (level.getServer().getWorldData().worldGenOptions().generateStructures()) {
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ChunkPos thisPos = new ChunkPos(entry.getPosition());
        if (registry.getTag(LootrTags.Structure.STRUCTURE_BLACKLIST).filter(tag -> tag.size() != 0).isPresent()) {
          if (LootrAPI.isTaggedStructurePresent(level, thisPos, LootrTags.Structure.STRUCTURE_BLACKLIST, entry.getPosition())) {
            iterator.remove();
            continue;
          }
        } else if (registry.getTag(LootrTags.Structure.STRUCTURE_WHITELIST).filter(tag -> tag.size() != 0)
            .isPresent()) {
          if (!LootrAPI.isTaggedStructurePresent(level, thisPos, LootrTags.Structure.STRUCTURE_WHITELIST, entry.getPosition())) {
            iterator.remove();
            continue;
          }
        }
      }

      BlockEntity blockEntity = level.getBlockEntity(entry.getPosition());
      if (!(blockEntity instanceof RandomizableContainerBlockEntity be) || LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity) {
        iterator.remove();
        continue;
      }
      if (be.getLootTable() == null || LootrAPI.isLootTableBlacklisted(be.getLootTable())) {
        iterator.remove();
        continue;
      }
      BlockState stateAt = level.getBlockState(entry.getPosition());
      BlockState replacement = LootrAPI.replacementBlockState(stateAt);
      if (replacement == null) {
        iterator.remove();
        continue;
      }
      // Save specific data. Currently, this includes the LockCode (all platforms), along with NeoForge's getPersistentData.
      DataToCopy data = PlatformAPI.copySpecificData(be);
      ResourceKey<LootTable> table = be.getLootTable();
      long seed = be.getLootTableSeed();
      // IMPORTANT: Clear loot table to prevent loot drop when container is destroyed
      be.setLootTable(null);
      level.destroyBlock(entry.getPosition(), false);
      level.setBlock(entry.getPosition(), replacement, 2);
      BlockEntity newBlockEntity = level.getBlockEntity(entry.getPosition());
      PlatformAPI.restoreSpecificData(data, newBlockEntity);
      if (LootrAPI.resolveBlockEntity(newBlockEntity) instanceof ILootrBlockEntity && newBlockEntity instanceof RandomizableContainerBlockEntity rbe) {
        rbe.setLootTable(table, seed);
      } else {
        LootrAPI.LOG.error("replacement {} is not an ILootrBlockEntity {} at {}", replacement, entry.getDimension(), entry.getPosition());
      }

      iterator.remove();
    }
    synchronized (pendingEntries) {
      blockEntityEntries.addAll(pendingEntries);
      pendingEntries.clear();
    }
  }

  public record Entry(ResourceKey<Level> dimension, BlockPos position, Set<ChunkPos> chunks, long addedAt) {
    public ResourceKey<Level> getDimension() {
      return dimension();
    }

    public BlockPos getPosition() {
      return position();
    }

    public Set<ChunkPos> getChunkPositions() {
      return chunks();
    }

    public long age(MinecraftServer server) {
      return server.getTickCount() - addedAt;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Entry entry = (Entry) o;

      if (!dimension.equals(entry.dimension)) return false;
      return position.equals(entry.position);
    }

    @Override
    public int hashCode() {
      int result = dimension.hashCode();
      result = 31 * result + position.hashCode();
      return result;
    }
  }
}
