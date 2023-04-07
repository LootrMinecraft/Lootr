package noobanidus.mods.lootr.block.tile;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.event.HandleWorldGen;

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

  public static void addEntry(World world, BlockPos position) {
    if(world.isRemote)
      return;

    if (ConfigManager.isDimensionBlocked(world.provider.getDimension())) {
      return;
    }

    WorldBorder border = world.getWorldBorder();
    if (!border.contains(position)) {
      return;
    }

    Entry newEntry = new Entry(world.provider.getDimension(), position);
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
      Set<Entry> toRemove = new ObjectLinkedOpenHashSet<>();
      Set<Entry> copy;
      synchronized (listLock) {
        tickingList = true;
        copy = new ObjectLinkedOpenHashSet<>(tileEntries);
        tickingList = false;
      }
      synchronized (worldLock) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(server == null)
          return;
        for (Entry entry : copy) {
          WorldServer level = DimensionManager.getWorld(entry.getDimension(), false);
          if (level == null || !level.getWorldBorder().contains(entry.getChunkPosition())) {
            toRemove.add(entry);
            continue;
          }
          ChunkProviderServer provider = level.getChunkProvider();
          ChunkPos pos = entry.getChunkPosition();
          Chunk chunk = (Chunk) provider.getLoadedChunk(pos.x, pos.z);
          if (chunk != null) {
            TileEntity tile = level.getTileEntity(entry.getPosition());
            if (!(tile instanceof TileEntityLockableLoot) || tile instanceof ILootTile) {
              toRemove.add(entry);
              continue;
            }
            TileEntityLockableLoot te = (TileEntityLockableLoot) tile;
            if (te.lootTable == null || ConfigManager.isBlacklisted(te.lootTable)) {
              toRemove.add(entry);
              continue;
            }
            ResourceLocation table = te.lootTable;
            long seed = te.lootTableSeed;
            IBlockState stateAt = level.getBlockState(entry.getPosition());
            IBlockState replacement = ConfigManager.replacement(stateAt);
            if (replacement == null) {
              toRemove.add(entry);
              continue;
            }
            te.clear();
            tile = HandleWorldGen.replaceOldLootBlockAt(chunk, entry.getPosition(), replacement);
            if (tile instanceof ILootTile) {
              ((TileEntityLockableLoot) tile).setLootTable(table, seed);
            } else {
              Lootr.LOG.error("replacement TE " + tile + " is not an ILootTile " + entry.getDimension() + " at " + entry.getPosition());
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
    private final int dimension;
    private final BlockPos position;
    private final ChunkPos chunkPos;

    public Entry(int dimension, BlockPos position) {
      this.dimension = dimension;
      this.position = position;
      this.chunkPos = new ChunkPos(position);
    }

    public int getDimension() {
      return dimension;
    }

    public BlockPos getPosition() {
      return position;
    }

    public ChunkPos getChunkPosition() {
      return chunkPos;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Entry entry = (Entry) o;

      if (dimension != entry.dimension) return false;
      return position.equals(entry.position);
    }

    @Override
    public int hashCode() {
      int result = dimension;
      result = 31 * result + position.hashCode();
      return result;
    }
  }
}
