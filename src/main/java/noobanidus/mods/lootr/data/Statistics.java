package noobanidus.mods.lootr.data;

import java.util.Set;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.lootr.data.old.AdvancementData;
import noobanidus.mods.lootr.data.old.AdvancementData.UUIDPair;

public class Statistics
{
	Set<UUID> awards = new ObjectOpenHashSet<>();
	Set<UUID> stats = new ObjectOpenHashSet<>();
	
	public void award(UUID id) {
		awards.add(id);
	}
	
	public boolean hasAward(UUID id) {
		return awards.contains(id);
	}
	
	public void score(UUID id) {
		stats.add(id);
	}
	
	public boolean isScored(UUID id) {
		return stats.contains(id);
	}
		
	public CompoundNBT save(CompoundNBT nbt) {
		ListNBT list = new ListNBT();
		for(UUID id : awards) {
			list.add(NBTUtil.createUUID(id));
		}
		if(list.size() > 0) {
			nbt.put("awards", list);
		}
		list = new ListNBT();
		for(UUID id : stats) {
			list.add(NBTUtil.createUUID(id));
		}
		if(list.size() > 0) {
			nbt.put("scores", list);
		}
		return nbt;
	}
	
	public void load(CompoundNBT nbt) {
	    ListNBT list = nbt.getList("awards", Constants.NBT.TAG_INT_ARRAY);
	    for(INBT data : list) {
	    	awards.add(NBTUtil.loadUUID(data));
	    }
	    list = nbt.getList("scores", Constants.NBT.TAG_INT_ARRAY);
	    for(INBT data : list) {
	    	stats.add(NBTUtil.loadUUID(data));
	    }
	}
}
