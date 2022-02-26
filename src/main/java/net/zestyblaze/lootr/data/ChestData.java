package net.zestyblaze.lootr.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.zestyblaze.lootr.api.LootFiller;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.entity.LootrChestMinecartEntity;
import net.zestyblaze.lootr.util.ServerAccessImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class ChestData extends SavedData {
    private final String key;
    private BlockPos pos;
    private ResourceKey<Level> dimension;
    private UUID uuid;
    private Map<UUID, SpecialChestInventory> inventories = new HashMap<>();
    private NonNullList<ItemStack> reference;
    private boolean custom;
    private boolean entity;

    protected ChestData(String key) {
        this.key = key;
    }

    public UUID getEntityId() {
        if (entity == false) {
            return null;
        }

        return uuid;
    }

    @Nullable
    public UUID getTileId() {
        if (!custom && entity) {
            return null;
        }

        return uuid;
    }

    public static String ID(UUID id) {
        String idString = id.toString();
        return "lootr/" + idString.charAt(0) + "/" + idString.substring(0, 2) + "/" + idString;
    }

    public static Supplier<ChestData> custom(ResourceKey<Level> dimension, UUID id, NonNullList<ItemStack> base) {
        return () -> {
            ChestData data = new ChestData(ID(id));
            data.pos = null;
            data.dimension = dimension;
            data.uuid = id;
            data.reference = base;
            data.custom = true;
            data.entity = false;
            if (data.reference == null) {
                throw new IllegalArgumentException("Both customId and inventory reference cannot be null.");
            }
            return data;
        };
    }

    public static Supplier<ChestData> id(ResourceKey<Level> dimension, UUID id) {
        return () -> {
            ChestData data = new ChestData(ID(id));
            data.pos = null;
            data.dimension = dimension;
            data.uuid = id;
            data.entity = false;
            data.reference = null;
            data.custom = false;
            return data;
        };
    }

    public static Supplier<ChestData> entity(UUID entityId) {
        return () -> {
            ChestData data = new ChestData(ID(entityId));
            data.pos = null;
            data.dimension = null;
            data.uuid = entityId;
            data.reference = null;
            data.custom = false;
            data.entity = true;
            return data;
        };
    }

    public LootFiller customInventory() {
        return (player, inventory, table, seed) -> {
            for (int i = 0; i < reference.size(); i++) {
                inventory.setItem(i, reference.get(i).copy());
            }
        };
    }

    public boolean clearInventory(UUID uuid) {
        return inventories.remove(uuid) != null;
    }

    @Nullable
    public SpecialChestInventory getInventory(ServerPlayer player, BlockPos pos) {
        SpecialChestInventory result = inventories.get(player.getUUID());
        if (result != null) {
            result.setBlockPos(pos);
        }
        return result;
    }

    public SpecialChestInventory createInventory(ServerPlayer player, LootFiller filler, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
        ServerLevel level = (ServerLevel) player.level;
        SpecialChestInventory result;
        if (level.dimension() != dimension) {
            MinecraftServer server = ServerAccessImpl.getServer();
            if (server == null) {
                return null;
            }
            level = server.getLevel(dimension);
        }

        if (level == null) {
            return null;
        }

        NonNullList<ItemStack> items = NonNullList.withSize(sizeSupplier.getAsInt(), ItemStack.EMPTY);
        result = new SpecialChestInventory(this, items, displaySupplier.get(), pos);
        filler.unpackLootTable(player, result, tableSupplier.get(), seedSupplier.getAsLong());
        inventories.put(player.getUUID(), result);
        setDirty();
        return result;
    }

    public SpecialChestInventory createInventory(ServerPlayer player, LootFiller filler, BaseContainerBlockEntity blockEntity, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
        ServerLevel level = (ServerLevel) player.level;
        SpecialChestInventory result;
        if(level.dimension() != dimension) {
            MinecraftServer server = ServerAccessImpl.getServer();
            if (server == null) {
                return null;
            }
            level = server.getLevel(dimension);
        }

        if(level == null) {
            return null;
        }

        NonNullList<ItemStack> items = NonNullList.withSize(blockEntity.getContainerSize(), ItemStack.EMPTY);
        result = new SpecialChestInventory(this, items, blockEntity.getDisplayName(), pos);
        filler.unpackLootTable(player, result, tableSupplier.get(), seedSupplier.getAsLong());
        inventories.put(player.getUUID(), result);
        setDirty();
        return result;
    }

    public SpecialChestInventory createInventory(ServerPlayer player, LootFiller filler, RandomizableContainerBlockEntity tile) {
        ServerLevel world = (ServerLevel) player.level;
        SpecialChestInventory result;
        long seed = -1;
        ResourceLocation lootTable;
        if (entity) {
            Entity initial = world.getEntity(uuid);
            if (!(initial instanceof LootrChestMinecartEntity cart)) {
                return null;
            }
            NonNullList<ItemStack> items = NonNullList.withSize(cart.getContainerSize(), ItemStack.EMPTY);
            result = new SpecialChestInventory(this, items, cart.getDisplayName(), pos);
            lootTable = cart.lootTable;
        } else {
            lootTable = ((ILootBlockEntity) tile).getTable();

            NonNullList<ItemStack> items = NonNullList.withSize(tile.getContainerSize(), ItemStack.EMPTY);
            result = new SpecialChestInventory(this, items, tile.getDisplayName(), pos);
        }
        filler.unpackLootTable(player, result, lootTable, seed);
        inventories.put(player.getUUID(), result);
        setDirty();
        return result;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putString("key", this.key);
        compound.putLong("position", pos.asLong());
        compound.putString("dimension", dimension.location().toString());
        compound.putUUID("uuid", uuid);
        compound.putBoolean("custom", custom);
        compound.putBoolean("entity", entity);
        if(reference != null) {
            compound.putInt("referenceSize", reference.size());
            compound.put("reference", ContainerHelper.saveAllItems(new CompoundTag(), reference, true));
        }
        ListTag compounds = new ListTag();
        for(Map.Entry<UUID, SpecialChestInventory> entry : inventories.entrySet()) {
            CompoundTag thisTag = new CompoundTag();
            thisTag.putUUID("uuid", entry.getKey());
            thisTag.put("chest", entry.getValue().writeItems());
            thisTag.putString("name", entry.getValue().writeName());
            compounds.add(thisTag);
        }
        compound.put("inventories", compounds);

        return compound;
    }
}
