package noobanidus.mods.lootr.data;

import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class TimedData
{
	Object2LongMap<UUID> refreshTime = new Object2LongLinkedOpenHashMap<>();
	
	public void load(CompoundNBT compound)
	{
		ListNBT list = compound.getList("decay", Constants.NBT.TAG_COMPOUND);
		for(int i = 0,m=list.size();i<m;i++) {
			CompoundNBT data = list.getCompound(i);
			refreshTime.put(data.getUUID("id"), data.getLong("time"));
		}
	}
	
	public CompoundNBT save(CompoundNBT compound)
	{
		ListNBT list = new ListNBT();
		for(ObjectIterator<Entry<UUID>> iter = Object2LongMaps.fastIterator(refreshTime);iter.hasNext();) {
			Entry<UUID> entry = iter.next();
			CompoundNBT data = new CompoundNBT();
			data.putUUID("id", entry.getKey());
			data.putLong("time", entry.getLongValue());
			list.add(data);
		}
		if(list.size() > 0) {
			compound.put("decay", list);
		}
		return compound;
	}
	
	public void markRefresh(UUID id, long nextTime) {
		refreshTime.put(id, nextTime);
	}
	
	public boolean isRefreshed(UUID id, long currentTime) {
		long time = refreshTime.getLong(id);
		return time > 0 && time < currentTime;
	}
	
	public int getRefreshTimeLeft(UUID id, long currentTime) {
		return (int)(Math.max(refreshTime.getLong(id) - currentTime, -1));
	}
	
	public void removeRefresh(UUID id) {
		refreshTime.removeLong(id);
	}
}
