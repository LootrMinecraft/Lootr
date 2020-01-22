package noobanidus.mods.lootr.data;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class BooleanData extends WorldSavedData {
  private Int2ObjectOpenHashMap<Long2BooleanOpenHashMap> lootMap = new Int2ObjectOpenHashMap<>();
  private static final String ID = "Lootr-BooleanSaveData";

  public BooleanData() {
    super(ID);
  }

  public BooleanData(String name) {
    super(name);
  }

  public boolean isLootChest(DimensionType dim, BlockPos pos) {
    return isLootChest(dim.getId(), pos.toLong());
  }

  private boolean isLootChest(int dim, long pos) {
    Long2BooleanOpenHashMap dimMap = getDimension(dim);
    return dimMap.get(pos);
  }

  public void markLootChest(DimensionType dim, BlockPos pos) {
    markLootChest(dim.getId(), pos.toLong());
  }

  private void markLootChest(int dim, long pos) {
    Long2BooleanOpenHashMap dimMap = getDimension(dim);
    dimMap.put(pos, true);
    markDirty();
  }

  public void deleteLootChest(DimensionType dim, BlockPos pos) {
    deleteLootChest(dim.getId(), pos.toLong());
  }

  private void deleteLootChest(int dim, long pos) {
    Long2BooleanOpenHashMap dimMap = getDimension(dim);
    dimMap.remove(pos);
    markDirty();
  }

  private Long2BooleanOpenHashMap getDimension(int dim) {
    return lootMap.computeIfAbsent(dim, o -> {
      Long2BooleanOpenHashMap map = new Long2BooleanOpenHashMap();
      map.defaultReturnValue(false);
      return map;
    });
  }

  @Override
  public void read(CompoundNBT compound) {
    lootMap.clear();
    for (String key : compound.keySet()) {
      long[] positions = compound.getCompound(key).getLongArray("positions");
      byte[] values = compound.getCompound(key).getByteArray("values");
      if (positions.length != values.length) {
        throw new IllegalStateException("Illegal state: positions(" + positions.length + ") does not match values (" + values.length + ")");
      }
      Long2BooleanOpenHashMap dimMap = getDimension(Integer.parseInt(key));
      for (int i = 0; i < positions.length; i++) {
        dimMap.put(positions[i], values[i] == 1);
      }
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    for (Int2ObjectMap.Entry<Long2BooleanOpenHashMap> entry : lootMap.int2ObjectEntrySet()) {
      LongArrayList longs = new LongArrayList();
      ByteArrayList bools = new ByteArrayList();
      for (Long2BooleanMap.Entry sub : entry.getValue().long2BooleanEntrySet()) {
        longs.add(sub.getLongKey());
        bools.add(sub.getBooleanValue() ? (byte) 1 : (byte) 0);
      }
      CompoundNBT thisEntry = new CompoundNBT();
      thisEntry.putLongArray("positions", longs);
      thisEntry.putByteArray("values", bools.toArray(new byte[0]));
      compound.put(String.valueOf(entry.getIntKey()), thisEntry);
    }
    return compound;
  }

  private static ServerWorld getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD);
  }

  private static BooleanData getInstance() {
    return getServerWorld().getSavedData().getOrCreate(BooleanData::new, BooleanData.ID);
  }

  // Convenience methods

  public static boolean isLootChest(IWorld world, BlockPos pos) {
    return getInstance().isLootChest(world.getDimension().getType(), pos);
  }

  public static void markLootChest(IWorld world, BlockPos pos) {
    BooleanData data = getInstance();
    data.markLootChest(world.getDimension().getType(), pos);
    getServerWorld().getSavedData().save();
  }

  public static void deleteLootChest(IWorld world, BlockPos pos) {
    BooleanData data = getInstance();
    data.deleteLootChest(world.getDimension().getType(), pos);
    getServerWorld().getSavedData().save();
  }

}
