package noobanidus.mods.lootr.block.entities;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.util.StructureUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class TileTicker {
  private final static Object listLock = new Object();
  private final static Object worldLock = new Object();
  private static boolean tickingList = false;
  private final static Set<Entry> tileEntries = new LinkedHashSet<>();
  private final static Set<Entry> pendingEntries = new LinkedHashSet<>();

  public static void addEntry(ResourceKey<Level> dimension, BlockPos position) {
    if (ConfigManager.isDimensionBlocked(dimension)) {
      return;
    }
    Entry newEntry = new Entry(dimension, position);
    synchronized (listLock) {
      if (tickingList) {
        pendingEntries.add(newEntry);
      } else {
        tileEntries.add(newEntry);
      }
    }
  }

  @SubscribeEvent
  public static void serverTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      Set<Entry> toRemove = new HashSet<>();
      Set<Entry> copy;
      synchronized (listLock) {
        tickingList = true;
        copy = new HashSet<>(tileEntries);
        tickingList = false;
      }
      synchronized (worldLock) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        outer: for (Entry entry : copy) {
          ServerLevel level = server.getLevel(entry.getDimension());
          if (level == null) {
            throw new IllegalStateException("got a null world for tile ticker in dimension " + entry.getDimension() + " at " + entry.getPosition());
          }
          ServerChunkCache provider = level.getChunkSource();
          CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> chunkFuture;
          for (ChunkPos pos : entry.getChunkPositions()) {
            chunkFuture = provider.getChunkFuture(pos.x, pos.z, ChunkStatus.FULL, false);
            if (!chunkFuture.isDone()) {
              continue outer;
            }
          }
          ChunkPos pos = entry.getChunkPosition();
          chunkFuture = provider.getChunkFuture(pos.x, pos.z, ChunkStatus.FULL, false);
          if (chunkFuture.isDone()) {
            BlockEntity blockEntity = level.getBlockEntity(entry.getPosition());
            if (!(blockEntity instanceof RandomizableContainerBlockEntity be) || blockEntity instanceof ILootBlockEntity) {
              toRemove.add(entry);
              continue;
            }
            if (be.lootTable == null || ConfigManager.isBlacklisted(be.lootTable)) {
              toRemove.add(entry);
              continue;
            }
            if (!ConfigManager.getLootStructureBlacklist().isEmpty()) {
              StructureFeature<?> startAt = StructureUtil.featureFor(level, entry.getPosition());
              if (startAt != null && ConfigManager.getLootStructureBlacklist().contains(startAt.getRegistryName())) {
                toRemove.add(entry);
                continue;
              }
            }
            ResourceLocation table = be.lootTable;
            long seed = be.lootTableSeed;
            be.unpackLootTable(null);
            Clearable.tryClear(be);
            BlockState stateAt = level.getBlockState(entry.getPosition());
            BlockState replacement = ConfigManager.replacement(stateAt);
            if (replacement == null) {
              toRemove.add(entry);
              continue;
            }
            level.destroyBlock(entry.getPosition(), false);
            level.setBlock(entry.getPosition(), replacement, 2);
            blockEntity = level.getBlockEntity(entry.getPosition());
            if (blockEntity instanceof ILootBlockEntity) {
              ((RandomizableContainerBlockEntity) blockEntity).setLootTable(table, seed);
            } else {
              Lootr.LOG.error("replacement " + replacement + " is not an ILootTile " + entry.getDimension() + " at " + entry.getPosition());
            }

            toRemove.add(entry);
          }
        }
      }
      synchronized (listLock) {
        tickingList = true;
        tileEntries.removeAll(toRemove);
        tileEntries.addAll(pendingEntries);
        tickingList = false;
        pendingEntries.clear();
      }
    }
  }

  public static class Entry {
    private final ResourceKey<Level> dimension;
    private final BlockPos position;
    private final Set<ChunkPos> chunks = new HashSet<>();
    private final ChunkPos chunkPos;

    private static final List<BlockPos> offsets = Arrays.asList(new BlockPos(0, 0, 8), new BlockPos(8, 0, 0), new BlockPos(0, 0, -8), new BlockPos(-8, 0, 0), new BlockPos(8, 0, 8), new BlockPos(-8, 0, -8), new BlockPos(8, 0, -8), new BlockPos(-8, 0, 8));

    public Entry(ResourceKey<Level> dimension, BlockPos position) {
      this.dimension = dimension;
      this.position = position;
      this.chunkPos = new ChunkPos(this.position);

      for (BlockPos offset : offsets) {
        ChunkPos pos = new ChunkPos(position.offset(offset));
        if (!this.chunkPos.equals(pos)) {
          chunks.add(pos);
        }
      }
    }

    public ResourceKey<Level> getDimension() {
      return dimension;
    }

    public BlockPos getPosition() {
      return position;
    }

    public Set<ChunkPos> getChunkPositions() {
      return chunks;
    }

    public ChunkPos getChunkPosition() {
      return chunkPos;
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
