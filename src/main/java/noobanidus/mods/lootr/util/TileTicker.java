package noobanidus.mods.lootr.util;

import com.google.common.base.Ticker;
import net.minecraft.block.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class TileTicker {
  private static int MAX_COUNTER = 10 * 20;
  private static final Object lock = new Object();

  private static boolean ticking = false;
  private static final LinkedList<Ticker> tickList = new LinkedList<>();
  private static final LinkedList<Ticker> waitList = new LinkedList<>();

  @SubscribeEvent
  public static void tick(TickEvent event) {
    ticking = true;
    if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END) {
      synchronized (lock) {
        ticking = true;
        tickList.clear();
        ticking = false;
      }
    } else if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
      synchronized (lock) {
        ticking = true;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Iterator<Ticker> iterator = tickList.iterator();
        while (iterator.hasNext()) {
          Ticker ticker = iterator.next();
          if (ticker.run(server)) {
            iterator.remove();
          } else if (ticker.invalid(server)) {
            iterator.remove();
          } else if (ticker.getCounter() > MAX_COUNTER) {
            iterator.remove();
          }
        }
        tickList.addAll(waitList);
        ticking = false;
      }
    }
    waitList.clear();
  }

  public static void addTicker(Ticker ticker) {
    synchronized (lock) {
      if (ticking) {
        waitList.add(ticker);
      } else {
        tickList.add(ticker);
      }
    }
  }

  public static void addTicker (TileEntity tile) {
    synchronized (lock) {
      Ticker ticker = new Ticker(tile);
      if (ticking) {
        waitList.add(ticker);
      } else {
        tickList.add(ticker);
      }
    }
  }

  public static void addTicker (BlockPos pos, DimensionType type, ResourceLocation table, long seed) {
    synchronized (lock) {
      Ticker ticker = new Ticker(pos, type, table, seed);
      if (ticking) {
        waitList.add(ticker);
      } else {
        tickList.add(ticker);
      }
    }
  }

  public static class Ticker {
    private final WeakReference<TileEntity> ref;
    private int counter = 0;
    private long seed;
    private ResourceLocation table;
    private BlockPos pos = null;
    private DimensionType dim = null;

    public Ticker(TileEntity tile, ResourceLocation table, long seed) {
      this.ref = new WeakReference<>(tile);
      this.table = table;
      this.seed = seed;
    }

    public Ticker(TileEntity tile) {
      this.ref = new WeakReference<>(tile);
      this.table = null;
      this.seed = -15;
    }

    public Ticker (BlockPos pos, DimensionType dimension, ResourceLocation table, long seed) {
      this.ref = null;
      this.table = table;
      this.seed = seed;
      this.pos = pos;
      this.dim = dimension;
    }

    @Nullable
    private TileEntity resolveTile (MinecraftServer server) {
      World world = server.getWorld(dim);
      return world.getTileEntity(pos);
    }

    public boolean resolveTable (MinecraftServer server) {
      if (this.table != null) {
        return true;
      }

      TileEntity te;

      if (ref == null) {
        te = resolveTile(server);
      } else {
        te = ref.get();
      }

      if (te == null) {
        return false;
      }

      if (te.getWorld() == null) {
        return false;
      }

      if (te.getWorld().isRemote()) {
        return false;
      }

      if (te instanceof LockableLootTileEntity) {
        LockableLootTileEntity tile = (LockableLootTileEntity) te;
      }

      return false;
    }

    public int getCounter() {
      return counter;
    }

    public boolean invalid(MinecraftServer server) {
      TileEntity te;

      if (ref == null) {
        te = resolveTile(server);
      } else {
        te = ref.get();
      }

      if (te == null) {
        return false;
      }

      if (te.getWorld() == null) {
        return false;
      }

      return te.getWorld().isRemote();
    }

    public boolean run(MinecraftServer server) {
      TileEntity te;

      if (ref == null) {
        te = resolveTile(server);
      } else {
        te = ref.get();
      }

      if (te == null) {
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

      BlockPos pos = te.getPos();

      BlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      BlockState replacementState;
      if (block == Blocks.CHEST) {
        replacementState = ModBlocks.CHEST.getDefaultState().with(ChestBlock.FACING, state.get(ChestBlock.FACING)).with(ChestBlock.WATERLOGGED, state.get(ChestBlock.WATERLOGGED));
      } else if (block == Blocks.TRAPPED_CHEST) {
        replacementState = ModBlocks.TRAPPED_CHEST.getDefaultState().with(ChestBlock.FACING, state.get(ChestBlock.FACING)).with(ChestBlock.WATERLOGGED, state.get(ChestBlock.WATERLOGGED));
      } else if (block == Blocks.BARREL) {
        replacementState = ModBlocks.BARREL.getDefaultState().with(BarrelBlock.PROPERTY_FACING, state.get(BarrelBlock.PROPERTY_FACING)).with(BarrelBlock.PROPERTY_OPEN, state.get(BarrelBlock.PROPERTY_OPEN));
      } else {
        replacementState = ModBlocks.CHEST.getDefaultState();
        if (state.has(BlockStateProperties.WATERLOGGED)) {
          replacementState = replacementState.with(BlockStateProperties.WATERLOGGED, state.get(BlockStateProperties.WATERLOGGED));
        }
      }

      world.setBlockState(pos, replacementState);
      te = world.getTileEntity(pos);
      if (te instanceof LockableLootTileEntity && te instanceof ILootTile) {
        ((LockableLootTileEntity) te).setLootTable(table, seed);
      }
      return true;
    }
  }
}
