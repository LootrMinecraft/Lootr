package noobanidus.mods.lootr.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class TickManager {
  public static final Map<GlobalPos, ResourceLocation> lootMap = Collections.synchronizedMap(new HashMap<>());

  @SuppressWarnings("FieldCanBeLocal")
  private static int MAX_COUNTER = 10 * 50;
  private static final Object writeLock = new Object();
  private static final Object listLock = new Object();
  private static boolean listTicking = false;
  private static final LinkedHashSet<ITicker> waitList = new LinkedHashSet<>();
  private static final LinkedHashSet<ITicker> tickList = new LinkedHashSet<>();
  private static int integrated = 0;

  @SubscribeEvent
  public static void tick(TickEvent event) {
    if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.CLIENT) {
      if (integrated == 0) {
        synchronized (listLock) {
          listTicking = true;
          tickList.clear();
          listTicking = false;
        }
      }
    } else if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.SERVER) {
      if (integrated == -1) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
          integrated = 1;
        } else {
          integrated = 0;
        }
      }
      Set<ITicker> listCopy = new HashSet<>();
      Set<ITicker> removed = new HashSet<>();
      synchronized (listLock) {
        listTicking = true;
        listCopy.clear();
        listCopy.addAll(tickList);
        listTicking = false;
      }
      synchronized (writeLock) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (!listCopy.isEmpty()) {
          for (ITicker ticker : listCopy) {
            if (ticker.getCounter() > MAX_COUNTER) {
              removed.add(ticker);
              Lootr.LOG.debug("Ticker expired: " + ticker);
              continue;
            }
            if (ticker.run()) {
              Lootr.LOG.debug("Successfully executed ticker: " + ticker);
              removed.add(ticker);
              continue;
            }
            if (ticker.invalid()) {
              removed.add(ticker);
              Lootr.LOG.debug("Invalid ticker removed: " + ticker);
            }
          }
        }
      }
      synchronized (listLock) {
        listTicking = true;
        if (removed.size() > 0) {
          Lootr.LOG.debug("Removed " + removed.size() + " tickers: " + removed);
        }
        tickList.removeAll(removed);
        tickList.addAll(waitList);
        listTicking = false;
        waitList.clear();
      }
    }
  }

  public static void addTicker(ITicker ticker) {
    synchronized (listLock) {
      if (listTicking) {
        //Lootr.LOG.debug("Adding new ticker to the wait list: " + ticker);
        waitList.add(ticker);
      } else {
        //Lootr.LOG.debug("Adding new ticker to the tick list: " + ticker);
        tickList.add(ticker);
      }
    }
  }

  public static void addTicker(TileEntity tile, BlockPos pos, DimensionType type, ResourceLocation table, long seed) {
    addTicker(new TileTicker(tile, pos, type, table, seed));
  }

  public static void addTicker(ContainerMinecartEntity entity, long seed, ResourceLocation table, BlockPos pos, DimensionType dim) {
    addTicker(new EntityTicker(entity, seed, table, pos, dim));
  }

  public static void trackTile(TileEntity te, ResourceLocation table, @Nullable DimensionType type) {
    synchronized (lootMap) {
      lootMap.put(GlobalPos.of(type, te.getPos()), table);
    }
  }

  public static void trackTile(TileEntity te, ResourceLocation table) {
    trackTile(te, table, null);
  }

  public interface ITicker {
    int getCounter();

    boolean invalid();

    boolean run();
  }

  public static class EntityTicker implements ITicker {
    private final ContainerMinecartEntity entity;
    private int counter = 0;
    private long seed;
    private ResourceLocation table;
    private BlockPos pos;
    private DimensionType dim;

    public EntityTicker(ContainerMinecartEntity entity, long seed, ResourceLocation table, BlockPos pos, DimensionType dim) {
      this.entity = entity;
      this.seed = seed;
      this.table = table;
      this.pos = pos;
      this.dim = dim;
    }

    @Override
    public int getCounter() {
      return counter;
    }

    @Override
    public boolean invalid() {
      if (!entity.isAddedToWorld()) {
        return false;
      }

      return !entity.isAlive();
    }

    @Override
    public boolean run() {
      if (entity.ticksExisted <= 50) {
        return false;
      }

      if (!entity.isAddedToWorld()) {
        return false;
      }

      if (!entity.world.isAreaLoaded(pos, 1)) {
        counter++;
        return false;
      }

      entity.dropContentsWhenDead(false);
      entity.remove();
      World world = entity.world;
      world.removeTileEntity(pos);
      //Lootr.LOG.debug("Calling setBlockState for entity ticker.");
      world.setBlockState(pos, ModBlocks.CHEST.getDefaultState());
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof ILootTile) {
        ((ILootTile) te).setTable(table);
        ((ILootTile) te).setSeed(seed);
        TickManager.trackTile(te, table, world.getDimension().getType());
      }


      return true;
    }

    @Override
    public String toString() {
      return "EntityTicker{" +
          "entity=" + entity +
          ", counter=" + counter +
          ", seed=" + seed +
          ", table=" + table +
          ", pos=" + pos +
          ", dim=" + dim +
          '}';
    }
  }

  public static class TileTicker implements ITicker {
    private final TileEntity ref;
    private int ticker = 50;
    private int counter = 0;
    private long seed;
    private ResourceLocation table;
    private BlockPos pos;
    private DimensionType dim;

    public TileTicker(TileEntity tile, BlockPos pos, @Nullable DimensionType dim, ResourceLocation table, long seed) {
      this.ref = tile;
      this.table = table;
      if (table == null) {
        this.table = null;
      }
      this.seed = seed;
      this.pos = pos;
      this.dim = dim;
    }

    public int getCounter() {
      return counter;
    }

    public boolean invalid() {
      TileEntity te = ref;

      if (!ChestUtil.isTileClass(te)) {
        return true;
      }

      if (te.getWorld() == null) {
        return false;
      }

      if (te.getWorld().isRemote()) {
        return true;
      }

      return !te.getWorld().isAreaLoaded(pos, 1);
    }

    public boolean run() {
      if (ticker-- > 0) {
        return false;
      }

      TileEntity te = ref;

      if (!ChestUtil.isTileClass(te)) {
        return false;
      }

      if (te.getWorld() == null) {
        counter++;
        return false; // still valid
      }

      World world = te.getWorld();
      if (world.isRemote()) {
        return false; // invalid
      }

      if (!world.isAreaLoaded(pos, 1)) {
        return false;
      }

      if (!world.chunkExists(pos.getX() >> 4, pos.getZ() >> 4)) {
        return false;
      }

      BlockPos pos = te.getPos();

      BlockState state = world.getBlockState(pos);
      Block block = state.getBlock();

      if (ChestUtil.hasReplacement(block)) {
        BlockState replacementState = ChestUtil.getReplacement(block, state);

        if (replacementState != null) {
          //Lootr.LOG.debug("Calling setBlockState to replace ticker.");
          world.removeTileEntity(pos);
          world.setBlockState(pos, replacementState);
        }
      } else {
        Lootr.LOG.debug("No replacement state found for: " + state);
      }
      te = world.getTileEntity(pos);
      if (te instanceof ILootTile) {
        ((ILootTile) te).setSeed(seed);
        ((ILootTile) te).setTable(table);
        TickManager.trackTile(te, table, world.getDimension().getType());
      } else if (te instanceof LockableLootTileEntity) {
        ((LockableLootTileEntity) te).lootTable = table;
        ((LockableLootTileEntity) te).lootTableSeed = seed;
      }
      return true;
    }

    @Override
    public String toString() {
      return "Ticker{" +
          "ref=" + ref +
          ", counter=" + counter +
          ", seed=" + seed +
          ", table=" + table +
          ", pos=" + pos +
          ", dim=" + dim +
          '}';
    }
  }

}

