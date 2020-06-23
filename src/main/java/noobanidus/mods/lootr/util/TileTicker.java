package noobanidus.mods.lootr.util;

import net.minecraft.block.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;

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
        Iterator<Ticker> iterator = tickList.iterator();
        while (iterator.hasNext()) {
          Ticker ticker = iterator.next();
          if (ticker.run()) {
            iterator.remove();
          } else if (ticker.invalid()) {
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

  public static class Ticker {
    private final WeakReference<TileEntity> ref;
    private int counter = 0;
    private long seed;
    private ResourceLocation table;

    public Ticker(TileEntity tile, ResourceLocation table, long seed) {
      ref = new WeakReference<>(tile);
    }

    public int getCounter() {
      return counter;
    }

    public boolean invalid() {
      final TileEntity te = ref.get();
      if (te == null) {
        return true;
      }

      if (te.getWorld() == null) {
        return false;
      }

      return te.getWorld().isRemote();
    }

    public boolean run() {
      TileEntity te = ref.get();
      if (te == null) { // invalid
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
