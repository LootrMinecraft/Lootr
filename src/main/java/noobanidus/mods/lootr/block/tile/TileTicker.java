package noobanidus.mods.lootr.block.tile;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class TileTicker {
  private final static Object listLock = new Object();
  private final static Object worldLock = new Object();
  private static boolean tickingList = false;
  private final static Set<Entry> tileEntries = new ObjectLinkedOpenHashSet<>();
  private final static Set<Entry> pendingEntries = new ObjectLinkedOpenHashSet<>();

  public static void addEntry(World level, BlockPos position) {
    if (level.isClientSide() || ServerLifecycleHooks.getCurrentServer() == null) {
      return;
    }

    RegistryKey<World> dimension = level.dimension();
    if (ConfigManager.isDimensionBlocked(dimension)) {
      return;
    }

    WorldBorder border = level.getWorldBorder();
    if (!border.isWithinBounds(position)) {
      return;
    }

    Entry newEntry = new Entry(dimension, position, new ChunkPos(position), ServerLifecycleHooks.getCurrentServer().getTickCount());
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
        ServerWorld level = server.getLevel(entry.getDimension());
        if (level == null || !level.getWorldBorder().isWithinBounds(entry.getChunkPosition()) || entry.age(server) > ConfigManager.MAXIMUM_AGE.get()) {
          toRemove.add(entry);
          continue;
        }

        ServerChunkProvider provider = level.getChunkSource();
        ChunkPos pos = entry.getChunkPosition();
        // TODO: On some servers this can cause chunk loading???
        Chunk chunk = (Chunk) provider.getChunk(pos.x, pos.z, ChunkStatus.FULL, false);
        if (chunk != null) {
          TileEntity tile = level.getBlockEntity(entry.getPosition());
          if (!(tile instanceof LockableLootTileEntity) || tile instanceof ILootTile) {
            toRemove.add(entry);
            continue;
          }
          LockableLootTileEntity te = (LockableLootTileEntity) tile;
          if (te.lootTable == null || ConfigManager.isBlacklisted(te.lootTable)) {
            toRemove.add(entry);
            continue;
          }
          ResourceLocation table = te.lootTable;
          long seed = te.lootTableSeed;
          BlockState stateAt = level.getBlockState(entry.getPosition());
          BlockState replacement = ConfigManager.replacement(stateAt);
          if (replacement == null) {
            toRemove.add(entry);
            continue;
          }
          chunk.pendingBlockEntities.remove(entry.getPosition());
          level.removeBlockEntity(entry.getPosition());
          level.setBlock(entry.getPosition(), replacement, 2);
          tile = level.getBlockEntity(entry.getPosition());
          if (tile instanceof ILootTile) {
            ((LockableLootTileEntity) tile).setLootTable(table, seed);
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

  public static class Entry {
    private final RegistryKey<World> dimension;
    private final BlockPos position;
    private final ChunkPos chunkPos;
    private final long addedAt;

    public Entry(RegistryKey<World> dimension, BlockPos position, ChunkPos chunkPos, long addedAt) {
      this.dimension = dimension;
      this.position = position;
      this.chunkPos = chunkPos;
      this.addedAt = addedAt;
    }

    public RegistryKey<World> getDimension() {
      return dimension;
    }

    public BlockPos getPosition() {
      return position;
    }

    public ChunkPos getChunkPosition() {
      return chunkPos;
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
