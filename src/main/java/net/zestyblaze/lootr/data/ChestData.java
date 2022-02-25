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
import net.minecraft.world.level.saveddata.SavedData;
import net.zestyblaze.lootr.api.LootFiller;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
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
    private UUID entityId;
    private UUID tileId;
    private UUID customId;
    private Map<UUID, SpecialChestInventory> inventories = new HashMap<>();
    private NonNullList<ItemStack> reference;
    private boolean custom;

    protected ChestData(String key) {
        this.key = key;
    }

    public UUID getEntityId() {
        return entityId;
    }

    @Nullable
    public UUID getTileId() {
        if (entityId != null) {
            return entityId;
        }
        if (tileId != null) {
            return tileId;
        }
        if (customId != null) {
            return customId;
        }
        return null;
    }

    public static String ID(UUID id) {
        String idString = id.toString();
        return "lootr/" + idString.charAt(0) + "/" + idString.substring(0, 2) + "/" + idString;
    }

    public static Supplier<ChestData> ref_id(ResourceKey<Level> dimension, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
        return () -> {
            ChestData data = new ChestData(ID(id));
            data.pos = null;
            data.dimension = dimension;
            data.entityId = null;
            data.tileId = id;
            data.reference = base;
            data.custom = true;
            data.customId = customId;
            if (data.customId == null && data.reference == null) {
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
            data.entityId = null;
            data.tileId = id;
            data.reference = null;
            data.custom = false;
            data.customId = null;
            return data;
        };
    }

    public static Supplier<ChestData> entity(UUID entityId) {
        return () -> {
            ChestData data = new ChestData(ID(entityId));
            data.pos = null;
            data.dimension = null;
            data.tileId = null;
            data.entityId = entityId;
            data.reference = null;
            data.custom = false;
            data.customId = null;
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

    /*
    public SpecialChestInventory createInventory(ServerPlayer player, LootFiller filler, @Nullable RandomizableContainerBlockEntity tile) {
        ServerLevel world = (ServerLevel) player.level;
        SpecialChestInventory result;
        LootrChestMinecartEntity cart;
        long seed = -1;
        ResourceLocation lootTable;
        if (entityId != null) {
            Entity initial = world.getEntity(entityId);
            if (!(initial instanceof LootrChestMinecartEntity)) {
                return null;
            }
            cart = (LootrChestMinecartEntity) initial;
            NonNullList<ItemStack> items = NonNullList.withSize(cart.getContainerSize(), ItemStack.EMPTY);
            result = new SpecialChestInventory(this, items, cart.getDisplayName(), pos);
            lootTable = cart.lootTable;
        } else {
            if (world.dimension() != dimension) {
                MinecraftServer server = ServerAccessImpl.getServer();
                if (server == null) {
                    return null;
                }
                world = server.getLevel(dimension);
            }

            if (world == null || tile == null) {
                return null;
            }

            lootTable = ((ILootBlockEntity) tile).getTable();

            NonNullList<ItemStack> items = NonNullList.withSize(tile.getContainerSize(), ItemStack.EMPTY);
            result = new SpecialChestInventory(this, items, tile.getDisplayName(), pos);
        }
        filler.unpackLootTable(player, result, lootTable, seed);
        inventories.put(player.getUUID(), result);
        setDirty();
        return result;
    }

     */


    @Override
    public CompoundTag save(CompoundTag compound) {
        if(key != null) {
            compound.putString("key", this.key);
        }
        if(pos != null) {
            compound.putLong("position", pos.asLong());
        }
        if(dimension != null) {
            compound.putString("dimension", dimension.location().toString());
        }
        if(entityId != null) {
            compound.putUUID("entityId", entityId);
        }
        if(tileId != null) {
            compound.putUUID("tileId", tileId);
        }
        if(customId != null) {
            compound.putUUID("customId", customId);
        }
        compound.putBoolean("custom", custom);
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
