package noobanidus.mods.lootr.block.entities;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.event.HandleChunk;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID)
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
    if (event.phase != TickEvent.Phase.END) {
      return;
    }
    Set<Entry> toRemove = new ObjectLinkedOpenHashSet<>();
    Set<Entry> copy;
    synchronized (listLock) {
      tickingList = true;
      copy = new ObjectLinkedOpenHashSet<>(tileEntries);
      tickingList = false;
    }
    synchronized (worldLock) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      for (Entry entry : copy) {
        ServerLevel level = server.getLevel(entry.getDimension());
        if (level == null || entry.age(server) > ConfigManager.MAXIMUM_AGE.get() || !level.getWorldBorder().isWithinBounds(entry.getPosition())) {
          toRemove.add(entry);
          continue;
        }

        boolean skip = false;
        synchronized (HandleChunk.LOADED_CHUNKS) {
          Set<ChunkPos> loadedChunks = HandleChunk.LOADED_CHUNKS.get(entry.dimension);
          if (loadedChunks != null) {
            for (ChunkPos chunkPos : entry.getChunkPositions()) {
              if (!loadedChunks.contains(chunkPos)) {
                skip = true;
                break;
              }
            }
          }
        }
        if (skip) {
          continue;
        }
        BlockEntity blockEntity = level.getBlockEntity(entry.getPosition());
        if (!(blockEntity instanceof RandomizableContainerBlockEntity be) || blockEntity instanceof ILootBlockEntity) {
          toRemove.add(entry);
          continue;
        }
        if (be.lootTable == null || ConfigManager.isBlacklisted(be.lootTable)) {
          toRemove.add(entry);
          continue;
        }
        BlockState stateAt = level.getBlockState(entry.getPosition());
        BlockState replacement = ConfigManager.replacement(stateAt);
        if (replacement == null) {
          toRemove.add(entry);
          continue;
        }
        ResourceLocation table = be.lootTable;
        long seed = be.lootTableSeed;
        be.lootTable = null;
        level.destroyBlock(entry.getPosition(), false);
        level.setBlock(entry.getPosition(), replacement, 2);
        blockEntity = level.getBlockEntity(entry.getPosition());
        if (blockEntity instanceof ILootBlockEntity) {
          ((RandomizableContainerBlockEntity) blockEntity).setLootTable(table, seed);
        } else {
          LootrAPI.LOG.error("replacement " + replacement + " is not an ILootTile " + entry.getDimension() + " at " + entry.getPosition());
        }

        toRemove.add(entry);
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

  public static class Entry {
    private final ResourceKey<Level> dimension;
    private final BlockPos position;
    private final Set<ChunkPos> chunks = new HashSet<>();
    private final long addedAt;

    public Entry(ResourceKey<Level> dimension, BlockPos position) {
      this.dimension = dimension;
      this.position = position;

      ChunkPos chunkPos = new ChunkPos(this.position);

      int oX = chunkPos.x;
      int oZ = chunkPos.z;
      chunks.add(chunkPos);

      for (int x = -2; x <= 2; x++) {
        for (int z = -2; z <= 2; z++) {
          chunks.add(new ChunkPos(oX + x, oZ + z));
        }
      }

      this.addedAt = ServerLifecycleHooks.getCurrentServer().getTickCount();
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
