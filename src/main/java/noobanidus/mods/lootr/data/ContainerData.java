package noobanidus.mods.lootr.data;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.data.old.SpecialChestInventory;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

public class ContainerData
{
	private BlockPos pos;
	private RegistryKey<World> dimension;
	private UUID entityId;
	private UUID tileId;
	private UUID customId;
	private SpecialChestInventory inventory;
	private NonNullList<ItemStack> reference;
	private boolean custom;
	
	public ContainerData()
	{
	}
	
	public ContainerData(RegistryKey<World> dimension, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base)
	{
		this.dimension = dimension;
		this.tileId = id;
		this.reference = base;
		this.custom = true;
		this.customId = customId;
		if(customId == null && base == null)
		{
			throw new IllegalArgumentException("Both customId and inventory reference cannot be null.");
		}
	}
	
	public ContainerData(RegistryKey<World> dimension, UUID id)
	{
		this.dimension = dimension;
		this.tileId = id;
	}
	
	public ContainerData(RegistryKey<World> dimension, BlockPos pos)
	{
		this.pos = pos;
		this.dimension = dimension;
	}
	
	public ContainerData(UUID entityId)
	{
		this.entityId = entityId;
	}
	
	public UUID getEntityId()
	{
		return entityId;
	}
	
	public LootFiller customInventory()
	{
		return (player, inventory, table, seed) -> {
			for(int i = 0;i < reference.size();i++)
			{
				inventory.setItem(i, reference.get(i).copy());
			}
		};
	}
	
	public SpecialChestInventory getInventory()
	{
		return inventory;
	}
	
	public SpecialChestInventory createInventory(ServerPlayerEntity player, LootFiller filler, @Nullable LockableLootTileEntity tile)
	{
		ServerWorld world = (ServerWorld)player.level;
		if(entityId != null)
		{
			Entity initial = world.getEntity(entityId);
			if(!(initial instanceof LootrChestMinecartEntity))
			{
				return null;
			}
			LootrChestMinecartEntity cart = (LootrChestMinecartEntity)initial;
			NonNullList<ItemStack> items = NonNullList.withSize(cart.getContainerSize(), ItemStack.EMPTY);
			// Saving this is handled elsewhere
			SpecialChestInventory result = new SpecialChestInventory(this, items, cart.getDisplayName(), pos);
			filler.fillWithLoot(player, result, cart.lootTable, -1);
			inventory = result;
			return result;
		}
		else
		{
			if(world.dimension() != dimension)
			{
				world = world.getServer().getLevel(dimension);
			}
			if(world == null || tile == null)
			{
				return null;
			}
			NonNullList<ItemStack> items = NonNullList.withSize(tile.getContainerSize(), ItemStack.EMPTY);
			// Saving this is handled elsewhere
			SpecialChestInventory result = new SpecialChestInventory(this, items, tile.getDisplayName(), pos);
			filler.fillWithLoot(player, result, ((ILootTile)tile).getTable(), -1);
			inventory = result;
			return result;
		}
	}
	
	public void load(CompoundNBT compound)
	{
		if(compound.contains("position"))
		{
			pos = BlockPos.of(compound.getLong("position"));
		}
		if(compound.contains("dimension"))
		{
			dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("dimension")));
		}
		if(compound.hasUUID("entityId"))
		{
			entityId = compound.getUUID("entityId");
		}
		if(compound.hasUUID("tileId"))
		{
			tileId = compound.getUUID("tileId");
		}
		if(compound.contains("custom"))
		{
			custom = compound.getBoolean("custom");
		}
		if(compound.hasUUID("customId"))
		{
			customId = compound.getUUID("customId");
		}
		if(compound.contains("reference") && compound.contains("referenceSize"))
		{
			int size = compound.getInt("referenceSize");
			reference = NonNullList.withSize(size, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(compound.getCompound("reference"), reference);
		}
		if(compound.contains("inventory"))
		{
			CompoundNBT data = compound.getCompound("inventory");
			inventory = new SpecialChestInventory(this, data.getCompound("chest"), data.getString("name"), pos);
		}
	}
	
	public CompoundNBT save(CompoundNBT compound)
	{
		if(pos != null)
		{
			compound.putLong("position", pos.asLong());
		}
		if(dimension != null)
		{
			compound.putString("dimension", dimension.location().toString());
		}
		if(entityId != null)
		{
			compound.putUUID("entityId", entityId);
		}
		if(tileId != null)
		{
			compound.putUUID("tileId", tileId);
		}
		if(customId != null)
		{
			compound.putUUID("customId", customId);
		}
		if(custom)
		{
			compound.putBoolean("custom", custom);
		}
		if(reference != null)
		{
			compound.putInt("referenceSize", reference.size());
			compound.put("reference", ItemStackHelper.saveAllItems(new CompoundNBT(), reference, true));
		}
		if(inventory != null)
		{
			CompoundNBT data = new CompoundNBT();
			data.put("chest", inventory.writeItems());
			data.putString("name", inventory.writeName());
			compound.put("inventory", data);
		}
		return compound;
	}
	
	public void clear()
	{
		inventory = null;
	}
	
}
