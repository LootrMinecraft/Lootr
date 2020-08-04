package noobanidus.mods.lootr.util;

import com.google.common.base.Ticker;
import net.minecraft.block.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
import noobanidus.mods.lootr.events.HandleBreak;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class TileTicker {
  @SuppressWarnings("FieldCanBeLocal")
  private static int MAX_COUNTER = 10 * 50;
  private static int integrated = -1;
  private static final Set<Ticker> tickList = Collections.synchronizedSet(new HashSet<>());

  @SubscribeEvent
  public static void tick(TickEvent event) {
    if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.CLIENT) {
      if (integrated == 0) {
        tickList.clear();
      }
    } else if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.SERVER) {
      if (integrated == -1) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
          integrated = 1;
        } else {
          integrated = 0;
        }
      }
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (!tickList.isEmpty()) {
        //Lootr.LOG.info("Ticking the following tickers: " + tickList);
        Iterator<Ticker> iterator = tickList.iterator();
        while (iterator.hasNext()) {
          Ticker ticker = iterator.next();
          if (ticker.getCounter() > MAX_COUNTER) {
            //Lootr.LOG.info("Ticker expired: " + ticker);
            iterator.remove();
            continue;
          }
          if (ticker.run()) {
            iterator.remove();
            continue;
          }
          if (ticker.invalid()) {
            iterator.remove();
          }
        }
      }
    }
  }

  public static void addTicker(TileEntity tile, BlockPos pos, DimensionType type, ResourceLocation table, long seed) {
    tickList.add(new Ticker(tile, pos, type, table, seed));
  }

  public static class Ticker {
    private final TileEntity ref;
    private int counter = 0;
    private long seed;
    private ResourceLocation table;
    private BlockPos pos;
    private DimensionType dim;

    public Ticker(TileEntity tile, BlockPos pos, @Nullable DimensionType dim, ResourceLocation table, long seed) {
      this.ref = tile;
      this.table = table;
      this.seed = seed;
      this.pos = pos;
      this.dim = dim;
    }

    public int getCounter() {
      return counter;
    }

    public boolean invalid() {
      TileEntity te = ref;

      if (te.getWorld() == null) {
        return false;
      }

      if (te.getWorld().isRemote()) {
        return true;
      }

      return !te.getWorld().isAreaLoaded(pos, 1);
    }

    public boolean run() {
      TileEntity te = ref;

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

      BlockPos pos = te.getPos();

      BlockState state = world.getBlockState(pos);
      Block block = state.getBlock();

      BlockState replacementState = null;

      if (!HandleBreak.specialLootChests.contains(block)) {
        if (block == Blocks.CHEST) {
          replacementState = ModBlocks.CHEST.getDefaultState().with(ChestBlock.FACING, state.get(ChestBlock.FACING)).with(ChestBlock.WATERLOGGED, state.get(ChestBlock.WATERLOGGED));
        } else if (block == Blocks.TRAPPED_CHEST) {
          replacementState = ModBlocks.TRAPPED_CHEST.getDefaultState().with(ChestBlock.FACING, state.get(ChestBlock.FACING)).with(ChestBlock.WATERLOGGED, state.get(ChestBlock.WATERLOGGED));
        } else if (block == Blocks.BARREL) {
          replacementState = ModBlocks.BARREL.getDefaultState().with(BarrelBlock.PROPERTY_FACING, state.get(BarrelBlock.PROPERTY_FACING)).with(BarrelBlock.PROPERTY_OPEN, state.get(BarrelBlock.PROPERTY_OPEN));
        }
      }

      if (replacementState != null) {
        world.setBlockState(pos, replacementState);
      }
      te = world.getTileEntity(pos);
      if (te instanceof ILootTile) {
        ((ILootTile) te).setSeed(seed);
        ((ILootTile) te).setTable(table);
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
