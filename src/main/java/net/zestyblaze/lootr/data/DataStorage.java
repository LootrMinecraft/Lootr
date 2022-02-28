package net.zestyblaze.lootr.data;

import net.minecraft.world.level.storage.DimensionDataStorage;
import net.zestyblaze.lootr.util.ServerAccessImpl;

import java.util.UUID;

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

    /*
    public static boolean isAwarded(UUID player, UUID tileId) {
        DimensionDataStorage manager = DataStorage.getDataStorage();
        AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, ID);
        return data.contains(player, tileId);
    }

     */
}
