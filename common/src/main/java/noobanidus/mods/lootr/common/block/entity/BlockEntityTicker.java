package noobanidus.mods.lootr.common.block.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.DataToCopy;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.chunk.LoadedChunks;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class BlockEntityTicker {
  private static final Map<ResourceKey<Level>, BlockEntityTicker> TICKERS = new Object2ObjectOpenHashMap<>();

  private final ResourceKey<Level> levelKey;
  private final Map<ChunkPos, Entry> blockEntityEntries = new Object2ObjectOpenHashMap<>();
  private final Map<ChunkPos, Entry> pendingEntries = new Object2ObjectOpenHashMap<>();

  private BlockEntityTicker(ResourceKey<Level> levelKey) {
    this.levelKey = levelKey;
  }

  public static void addEntity(BlockEntity entity, Level level, ChunkPos chunkPos) {
    if (LootrAPI.isDisabled()) {
      return;
    }
    ResourceKey<Level> dimension = getServerDimensionIfValid(level);
    if (dimension == null) {
      return;
    }

    BlockEntityTicker ticker;
    synchronized (TICKERS) {
      ticker = TICKERS.computeIfAbsent(dimension, BlockEntityTicker::new);
    }

    ticker.addEntity(level, entity, chunkPos);
  }

  private void addEntity(Level level, BlockEntity entity, ChunkPos chunkPos) {
    if (!LootrAPI.isWorldBorderSafe(level, chunkPos)) {
      return;
    }

    if (!isValidEntity(entity)) {
      return;
    }

    synchronized (pendingEntries) {
      Entry previousEntry = pendingEntries.get(chunkPos);
      if (previousEntry != null) {
        previousEntry.entityPositions.add(entity.getBlockPos());
      } else {
        HashSet<BlockPos> entityPositions = new HashSet<>();
        entityPositions.add(entity.getBlockPos());
        Entry entry = new Entry(chunkPos, entityPositions);
        pendingEntries.put(chunkPos, entry);
      }
    }
  }

  private static boolean isValidEntity(BlockEntity entity) {
    if (LootrTags.BlockEntity.isTagged(entity, LootrTags.BlockEntity.CONVERT_BLACKLIST)) {
      return false;
    }
    if (entity instanceof ILootrBlockEntity || LootrAPI.resolveBlockEntity(entity) instanceof ILootrBlockEntity) {
      return false;
    }
    ILootrDataAdapter<BlockEntity> adapter = LootrAPI.getAdapter(entity);
    // IMPORTANT: Do *not* check the loot table here as this is called before nbt is loaded, etc
    return adapter != null;
  }

  // As opposed to `isValidEntity`, this checks everything
  public static boolean isValidEntityFull(BlockEntity entity) {
    if (LootrAPI.isDisabled()) {
      return false;
    }

    if (LootrTags.BlockEntity.isTagged(entity, LootrTags.BlockEntity.CONVERT_BLACKLIST)) {
      return false;
    }

    Level level = entity.getLevel();
    BlockPos pos = entity.getBlockPos();

    if (level == null) {
      return false;
    }

    if (level.isClientSide() || level.getServer() == null || LootrAPI.getServer() == null || !(level instanceof ServerLevel serverLevel)) {
      return false;
    }

    if (entity instanceof ILootrBlockEntity || LootrAPI.resolveBlockEntity(entity) instanceof ILootrBlockEntity) {
      return false;
    }

    MinecraftServer server = level.getServer();
    if (!server.isSameThread()) {
      LootrAPI.LOG.error("Called isValidEntityFull on a non-server thread for {} in {}", entity, level.dimension());
      return false;
    }

    if (LootrAPI.isDimensionBlocked(serverLevel.dimension())) {
      return false;
    }

    // TODO: This is checked twice in theory but it might be faster to check it here
    if (!LootrAPI.isWorldBorderSafe(level, pos)) {
      return false;
    }

    if (LootrAPI.replacementBlockState(entity.getBlockState()) == null) {
      return false;
    }

    ILootrDataAdapter<BlockEntity> adapter = LootrAPI.getAdapter(entity);
    if (adapter == null) {
      return false;
    }

    ResourceKey<LootTable> lootTable = adapter.getLootTable(entity);
    if (lootTable == null) {
      return false;
    }

    if (LootrAPI.isLootTableBlacklisted(lootTable)) {
      return false;
    }

    Entry testEntry = new Entry(new ChunkPos(pos), Set.of(pos));
    Set<ChunkPos> loadedChunks = LoadedChunks.getLoadedChunks(level.dimension());
    if (testEntry.getChunkLoadStatus(serverLevel, loadedChunks) != ChunkLoadStatus.COMPLETE) {
      return false;
    }

    return true;
  }

  public static void onServerTick(MinecraftServer server) {
    if (LootrAPI.isDisabled()) {
      return;
    }

    for (BlockEntityTicker ticker : TICKERS.values()) {
      ServerLevel level = server.getLevel(ticker.levelKey);
      if (level == null) {
        continue;
      }
      ticker.onServerLevelTick(level);
    }
  }

  private void onServerLevelTick(ServerLevel level) {
    Set<ChunkPos> loadedChunks = LoadedChunks.getLoadedChunks(level.dimension());
    Iterator<Entry> iterator = blockEntityEntries.values().iterator();
    while (iterator.hasNext()) {
      Entry entry = iterator.next();
      switch (entry.getChunkLoadStatus(level, loadedChunks)) {
        case UNLOADED -> {
          // the chunk has unloaded. this entry is no longer valid, and it will be added again if the chunk loads again.
          iterator.remove();
        }
        case NOT_FULLY_LOADED -> {
          // keep waiting for the chunk to be fully loaded
        }
        case SURROUNDING_CHUNKS_NOT_LOADED -> {
          // keep waiting for the surrounding chunks to load
        }
        case COMPLETE -> {
          replaceEntitiesInChunk(level, entry);
          iterator.remove();
        }
      }
    }

    synchronized (pendingEntries) {
      for (Entry entry : pendingEntries.values()) {
        blockEntityEntries.merge(entry.chunkPos, entry, (entry1, entry2) -> {
          entry1.entityPositions.addAll(entry2.entityPositions);
          return entry1;
        });
      }

      pendingEntries.clear();
    }
  }

  private static boolean checkStructureValidity(ServerLevel level, ChunkPos chunkPos, BlockPos position) {
    if (!level.getServer().getWorldData().worldGenOptions().generateStructures()) {
      return true;
    }
    Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
    if (registry.getTag(LootrTags.Structure.STRUCTURE_BLACKLIST).filter(tag -> tag.size() != 0).isPresent()) {
      return !LootrAPI.isTaggedStructurePresent(level, chunkPos, LootrTags.Structure.STRUCTURE_BLACKLIST, position);
    } else if (registry.getTag(LootrTags.Structure.STRUCTURE_WHITELIST).filter(tag -> tag.size() != 0).isPresent()) {
      return LootrAPI.isTaggedStructurePresent(level, chunkPos, LootrTags.Structure.STRUCTURE_WHITELIST, position);
    }
    return true;
  }

  private static void replaceEntitiesInChunk(ServerLevel level, Entry entry) {
    for (BlockPos entityPos : entry.entityPositions()) {
      if (!checkStructureValidity(level, entry.chunkPos(), entityPos)) {
        continue;
      }
      BlockEntity blockEntity = level.getBlockEntity(entityPos);
      if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity) {
        continue;
      }

      ILootrDataAdapter<BlockEntity> adapter = LootrAPI.getAdapter(blockEntity);
      // I'm not sure how we could've reached this stage.
      if (adapter == null) {
        continue;
      }
      ResourceKey<LootTable> table = adapter.getLootTable(blockEntity);
      long seed = adapter.getLootSeed(blockEntity);
      if (table == null) {
        if (LootrAPI.shouldWarnNoLootTables()) {
          LootrAPI.LOG.warn("Potential block entity {} has no loot table in {} ({})", blockEntity, level.dimension(), entityPos);
        }
        continue;
      }
      if (LootrAPI.isLootTableBlacklisted(table)) {
        continue;
      }
      BlockState stateAt = level.getBlockState(entityPos);
      BlockState replacement = LootrAPI.replacementBlockState(stateAt);
      if (replacement == null) {
        continue;
      }

      replaceEntity(level, entityPos, adapter, blockEntity, replacement, table, seed);
    }
  }

  private static void replaceEntity(ServerLevel level, BlockPos entityPos, ILootrDataAdapter<BlockEntity> adapter, BlockEntity be, BlockState replacement, ResourceKey<LootTable> table, long seed) {
    LootrAPI.preProcess(level, entityPos, be, replacement, table, seed);
    // Save specific data. Currently, this includes the LockCode (all platforms), along with NeoForge's getPersistentData.
    DataToCopy data = PlatformAPI.copySpecificData(be);
    ItemStack itemCopy = ItemStack.EMPTY;
    if (adapter.hasCopyableComponentsViaItem(be)) {
      itemCopy = new ItemStack(be.getBlockState().getBlock());
      be.saveToItem(itemCopy, level.registryAccess());
    }
    // IMPORTANT: Clear loot table to prevent loot drop when container is destroyed
    adapter.setLootTable(be, null, 0);
    level.setBlock(entityPos, replacement, Block.UPDATE_CLIENTS);
    BlockEntity newBlockEntity = level.getBlockEntity(entityPos);
    if (LootrAPI.resolveBlockEntity(newBlockEntity) instanceof ILootrBlockEntity ibe) {
      if (!itemCopy.isEmpty()) {
        ibe.asBlockEntity().applyComponentsFromItemStack(itemCopy);
      }
      PlatformAPI.restoreSpecificData(data, newBlockEntity);
      ibe.setLootTableInternal(table, seed);
      if (PlatformAPI.shouldDoInitialSave()) {
        ibe.performUpdate();
      }
      LootrAPI.postProcess(level, entityPos, ibe.asBlockEntity(), replacement, table, seed);
    } else {
      LootrAPI.LOG.error("Somehow, replacement result {} is not an ILootrBlockEntity {} at {}", replacement, level.dimension(), entityPos);
    }
  }

  @Nullable
  private static ResourceKey<Level> getServerDimensionIfValid(Level level) {
    if (LootrAPI.getServer() == null || level.isClientSide()) {
      return null;
    }
    ResourceKey<Level> dimension = level.dimension();
    if (LootrAPI.isDimensionBlocked(dimension)) {
      return null;
    }
    return dimension;
  }

  public record Entry(ChunkPos chunkPos, Set<BlockPos> entityPositions) {
    public ChunkLoadStatus getChunkLoadStatus(ServerLevel level, Set<ChunkPos> loadedChunks) {
      ChunkSource chunkSource = level.getChunkSource();
      if (!LootrAPI.isWorldBorderSafe(level, chunkPos) || !chunkSource.hasChunk(chunkPos.x, chunkPos.z)) {
        return ChunkLoadStatus.UNLOADED;
      }
      if (!loadedChunks.contains(chunkPos)) {
        return ChunkLoadStatus.NOT_FULLY_LOADED;
      }

      for (int x = chunkPos.x - 2; x <= chunkPos.x + 2; x++) {
        for (int z = chunkPos.z - 2; z <= chunkPos.z + 2; z++) {
          if (x == chunkPos.x && z == chunkPos.z) {
            // this case is already checked above
            continue;
          }
          ChunkPos pos = new ChunkPos(x, z);
          // This has the potential to force-load chunks on the main thread
          // by ignoring the loading state of chunks outside the world border.
          if (!LootrAPI.isWorldBorderSafe(level, pos)) {
            continue;
          }
          if (!loadedChunks.contains(pos)) {
            return ChunkLoadStatus.SURROUNDING_CHUNKS_NOT_LOADED;
          }
        }
      }
      return ChunkLoadStatus.COMPLETE;
    }
  }

  public enum ChunkLoadStatus {
    UNLOADED,
    SURROUNDING_CHUNKS_NOT_LOADED,
    NOT_FULLY_LOADED,
    COMPLETE
  }
}
