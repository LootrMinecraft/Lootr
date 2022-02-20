package noobanidus.mods.lootr.data;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class PlayerData
{
	Statistics stats = new Statistics();
	TimedData time = new TimedData();
	Map<UUID, ContainerData> mappedContainers = new Object2ObjectLinkedOpenHashMap<>();
	
	public PlayerData()
	{
	}
	
	public CompoundNBT save(long currentTime)
	{
		CompoundNBT data = new CompoundNBT();
		putIfHasData(data, "stats", stats.save(new CompoundNBT()));
		putIfHasData(data, "timed", time.save(new CompoundNBT(), currentTime));
		ListNBT list = new ListNBT();
		for(Map.Entry<UUID, ContainerData> entry : mappedContainers.entrySet())
		{
			CompoundNBT compound = entry.getValue().save(new CompoundNBT());
			if(compound.isEmpty()) continue;
			compound.putUUID("storage_id", entry.getKey());
			list.add(compound);
		}
		if(list.size() > 0)  {
			data.put("inventories", list);
		}
		return data;
	}
	
	public PlayerData load(CompoundNBT nbt)
	{
		stats.load(nbt.getCompound("stats"));
		time.load(nbt.getCompound("timed"));
		ListNBT list = nbt.getList("inventories", Constants.NBT.TAG_COMPOUND);
		for(int i = 0;i<list.size();i++) {
			CompoundNBT data = list.getCompound(i);
			ContainerData container = new ContainerData();
			container.save(data);
			mappedContainers.put(data.getUUID("storage_id"), container);
		}
		return this;
	}
	
	private void putIfHasData(CompoundNBT data, String key, CompoundNBT toAdd)
	{
		if(toAdd.isEmpty()) return;
		data.put(key, toAdd);
	}
	
	public ContainerData getOrCreate(UUID id, Supplier<ContainerData> provider)
	{
		return mappedContainers.computeIfAbsent(id, T -> provider.get());
	}
	
	public TimedData getTimedData() {
		return time;
	}
	
	public Statistics getStats() {
		return stats;
	}
	
	public void clearPlayerData() {
		mappedContainers.clear();
	}
}
