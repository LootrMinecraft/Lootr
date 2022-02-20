package noobanidus.mods.lootr.data;

import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class DecayData extends WorldSavedData
{
	Object2LongMap<UUID> decayTime = new Object2LongLinkedOpenHashMap<>();
	
	public DecayData(String id)
	{
		super(id);
	}
	
	public static DecayData getDecay() {
		return new DecayData("Lootr-Decay-Data");
	}
	
	@Override
	public void load(CompoundNBT compound)
	{
		ListNBT list = compound.getList("decay", Constants.NBT.TAG_COMPOUND);
		for(int i = 0,m=list.size();i<m;i++) {
			CompoundNBT data = list.getCompound(i);
			decayTime.put(data.getUUID("id"), data.getLong("time"));
		}
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound)
	{
		ListNBT list = new ListNBT();
		for(ObjectIterator<Entry<UUID>> iter = Object2LongMaps.fastIterator(decayTime);iter.hasNext();) {
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
	
	public void markDecayed(UUID id, long nextTime) {
		decayTime.put(id, nextTime);	
		setDirty();
	}
	
	public boolean isDecayed(UUID id, long currentTime) {
		long time = decayTime.getLong(id);
		return time > 0 && time < currentTime;
	}
	
	public int getDecayTime(UUID id, long currentTime) {
		return (int)(Math.max(decayTime.getLong(id) - currentTime, -1));
	}
	
	public void removeDecay(UUID id) {
		if(decayTime.removeLong(id) != 0) {
			setDirty();
		}
	}
}
