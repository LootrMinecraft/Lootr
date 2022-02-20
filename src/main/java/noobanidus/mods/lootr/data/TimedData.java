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
	
	public CompoundNBT save(CompoundNBT nbt, long currentTime)
	{

		ListNBT list = saveMap(refreshTime, currentTime);
		if(list.size() > 0) {
			nbt.put("refresh", list);
		}
		return nbt;
	}
	
	public void load(CompoundNBT data) 
	{
		loadMap(refreshTime, data.getList("refresh", Constants.NBT.TAG_COMPOUND));
	}
	
	private void loadMap(Object2LongMap<UUID> map, ListNBT list) {
		for(int i = 0,m=list.size();i<m;i++) {
			CompoundNBT data = list.getCompound(i);
			map.put(data.getUUID("id"), data.getLong("time"));
		}
	}
	
	private ListNBT saveMap(Object2LongMap<UUID> map, long time) {
		ListNBT list = new ListNBT();
		for(ObjectIterator<Entry<UUID>> iter = Object2LongMaps.fastIterator(map);iter.hasNext();) {
			Entry<UUID> entry = iter.next();
			if(entry.getLongValue() < time) {
				iter.remove();
				continue;
			}
			CompoundNBT data = new CompoundNBT();
			data.putUUID("id", entry.getKey());
			data.putLong("time", entry.getLongValue());
			list.add(data);
		}
		return list;
	}

	
	public void markRefresh(UUID id, long nextTime) {
		refreshTime.put(id, nextTime);
	}
	
	public boolean isRefreshed(UUID id, long currentTime) {
		return refreshTime.getLong(id) < currentTime;
	}
	
	public int getRefreshTimeLeft(UUID id, long currentTime) {
		return (int)(currentTime - refreshTime.getLong(id));
	}
	
	public void removeRefresh(UUID id) {
		refreshTime.removeLong(id);
	}
}
