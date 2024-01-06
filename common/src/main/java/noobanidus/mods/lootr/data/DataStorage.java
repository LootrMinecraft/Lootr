package noobanidus.mods.lootr.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.util.ServerAccessImpl;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DataStorage {
    public static final String ID_OLD = "Lootr-AdvancementData";
    public static final String SCORED_OLD = "Lootr-ScoreData";
    public static final String DECAY_OLD = "Lootr-DecayData";
    public static final String REFRESH_OLD = "Lootr-RefreshData";

    public static final String ID = "lootr/" + ID_OLD;
    public static final String SCORED = "lootr/" + SCORED_OLD;
    public static final String DECAY = "lootr/" + DECAY_OLD;
    public static final String REFRESH = "lootr/" + REFRESH_OLD;

    public static DimensionDataStorage getDataStorage() {
        return ServerAccessImpl.getServer().overworld().getDataStorage();
    }

    public static boolean isAwarded(UUID player, UUID tileId) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, ID);
        return data.contains(player, tileId);
    }

    public static void award(UUID player, UUID tileId) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, ID);
        data.add(player, tileId);
        data.setDirty();
    }

    public static boolean isScored(UUID player, UUID tileId) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, SCORED);
        return data.contains(player, tileId);
    }

    public static void score(UUID player, UUID tileId) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, SCORED);
        data.add(player, tileId);
        data.setDirty();
    }

    public static int getDecayValue (UUID id) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
        return data.getValue(id);
    }

    public static boolean isDecayed(UUID id) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
        return data.isComplete(id);
    }

    public static void setDecaying (UUID id, int decay) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
        data.setValue(id, decay);
        data.setDirty();
    }

    public static void removeDecayed (UUID id) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
        if(data.remove(id) != -1) {
            data.setDirty();
        }
    }

    public static void doDecay () {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
        if(data.tick()) {
            data.setDirty();
        }
    }

    public static int getRefreshValue (UUID id) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
        return data.getValue(id);
    }

    public static boolean isRefreshed(UUID id) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
        return data.isComplete(id);
    }

    public static void setRefreshing (UUID id, int decay) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
        data.setValue(id, decay);
        data.setDirty();
    }

    public static void removeRefreshed (UUID id) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
        if(data.remove(id) != -1) {
            data.setDirty();
        }
    }

    public static void doRefresh () {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
        if(data.tick()) {
            data.setDirty();
        }
    }

    public static ChestData getInstanceUuid(ServerLevel world, BlockPos pos, UUID id) {
        return ChestData.unwrap(getDataStorage().computeIfAbsent(ChestData.loadWrapper(id, world.dimension(), pos), ChestData.id(world.dimension(), pos, id), ChestData.ID(id)), id, world.dimension(), pos);
    }

    public static ChestData getInstance(ServerLevel world, BlockPos pos, UUID id) {
        return ChestData.unwrap(getDataStorage().computeIfAbsent(ChestData.loadWrapper(id, world.dimension(), pos), ChestData.entity(world.dimension(), pos, id), ChestData.ID(id)), id, world.dimension(), pos);
    }

    public static ChestData getInstanceInventory(ServerLevel world, BlockPos pos, UUID id, NonNullList<ItemStack> base) {
        return ChestData.unwrap(getDataStorage().computeIfAbsent(ChestData.loadWrapper(id, world.dimension(), pos), ChestData.ref_id(world.dimension(), pos, id, base), ChestData.ID(id)), id, world.dimension(), pos);
    }

    @Nullable
    public static SpecialChestInventory getInventory (Level level, UUID uuid, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
        if(level.isClientSide() || !(level instanceof ServerLevel)) {
            return null;
        }

        ChestData data = getInstanceUuid((ServerLevel) level, pos, uuid);
        SpecialChestInventory inventory = data.getInventory(player);
        if(inventory == null) {
            inventory = data.createInventory(player, filler, sizeSupplier, displaySupplier, tableSupplier, seedSupplier);
        }

        return inventory;
    }

    @Nullable
    public static SpecialChestInventory getInventory (Level level, UUID uuid, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
        if(level.isClientSide() || !(level instanceof ServerLevel)) {
            return null;
        }

        ChestData data = getInstanceUuid((ServerLevel) level, pos, uuid);
        SpecialChestInventory inventory = data.getInventory(player);
        if(inventory == null) {
            inventory = data.createInventory(player, filler, blockEntity, tableSupplier, seedSupplier);
        }

        return inventory;
    }

    @Nullable
    public static SpecialChestInventory getInventory(Level world, UUID uuid, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity tile, LootFiller filler) {
        if(world.isClientSide || !(world instanceof ServerLevel)) {
            return null;
        }

        ChestData data = getInstanceUuid((ServerLevel) world, pos, uuid);
        SpecialChestInventory inventory = data.getInventory(player);
        if(inventory == null) {
            inventory = data.createInventory(player, filler, tile);
        }

        return inventory;
    }

    @Nullable
    public static SpecialChestInventory getInventory(Level world, UUID uuid, NonNullList<ItemStack> base, ServerPlayer player, BlockPos pos, RandomizableContainerBlockEntity tile) {
        if(world.isClientSide || !(world instanceof ServerLevel)) {
            return null;
        }
        ChestData data = getInstanceInventory((ServerLevel) world, pos, uuid, base);
        SpecialChestInventory inventory = data.getInventory(player);
        if(inventory == null) {
            inventory = data.createInventory(player, data.customInventory(), tile);
        }

        return inventory;
    }

    public static boolean clearInventories(UUID uuid) {
        ServerLevel world = ServerAccessImpl.getServer().overworld();
        DimensionDataStorage data = world.getDataStorage();
        Path dataPath = world.getServer().getWorldPath(new LevelResource("data")).resolve("lootr");

        // TODO: Clear
        List<String> ids = new ArrayList<>();
        try(Stream<Path> paths = Files.walk(dataPath)) {
            paths.forEach(o -> {
                if(Files.isRegularFile(o)) {
                    String fileName = o.getFileName().toString();
                    if(fileName.startsWith("Lootr-")) {
                        return;
                    }
                    ids.add("lootr/" + fileName.charAt(0) + "/" + fileName.substring(0, 2) + "/" + fileName.replace(".dat", ""));
                }
            });
        } catch (IOException e) {
            return false;
        }

        int cleared = 0;
        for(String id : ids) {
            ChestData chestData = data.get(ChestData::load, id);
            if(chestData != null) {
                if(chestData.clearInventory(uuid)) {
                    cleared++;
                    chestData.setDirty();
                }
            }
        }
        LootrAPI.LOG.info("Cleared " + cleared + " inventories for play UUID " + uuid.toString());
        return cleared != 0;
    }

    @Nullable
    public static SpecialChestInventory getInventory(Level world, LootrChestMinecartEntity cart, ServerPlayer player, LootFiller filler) {
        if(world.isClientSide || !(world instanceof ServerLevel)) {
            return null;
        }

        ChestData data = getInstance((ServerLevel) world, cart.blockPosition(), cart.getUUID());
        SpecialChestInventory inventory = data.getInventory(player);
        if(inventory == null) {
            inventory = data.createInventory(player, filler, null);
        }

        return inventory;
    }

    public static void refreshInventory(Level level, BlockPos pos, UUID uuid, ServerPlayer player) {
        if(level.isClientSide() || !(level instanceof ServerLevel)) {
            return;
        }

        ChestData data = getInstanceUuid((ServerLevel) level, pos, uuid);
        data.clear();
        data.setDirty();
    }

    public static void refreshInventory(Level world, BlockPos pos, UUID uuid, NonNullList<ItemStack> base, ServerPlayer player) {
        if(world.isClientSide() || !(world instanceof ServerLevel)) {
            return;
        }
        ChestData data = getInstanceInventory((ServerLevel) world, pos, uuid, base);
        data.clear();
        data.setDirty();
    }

    public static void refreshInventory(Level world, LootrChestMinecartEntity cart, ServerPlayer player) {
        if(world.isClientSide() || !(world instanceof ServerLevel)) {
            return;
        }

        ChestData data = getInstance((ServerLevel) world, cart.blockPosition(), cart.getUUID());
        data.clear();
        data.setDirty();
    }
}
