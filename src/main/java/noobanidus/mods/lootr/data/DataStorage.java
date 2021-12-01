package noobanidus.mods.lootr.data;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

public class DataStorage {
  public static final String ID = "Lootr-AdvancementData";
  public static final String SCORED = "Lootr-ScoreData";

  public static boolean isAwarded(UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(ID), ID);

    return data.contains(player, tileId);
  }

  public static void award (UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(ID), ID);
    data.add(player, tileId);
    data.setDirty();
    manager.save();
  }

  public static boolean isScored(UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(SCORED), SCORED);

    return data.contains(player, tileId);
  }

  public static void score (UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(SCORED), SCORED);
    data.add(player, tileId);
    data.setDirty();
    manager.save();
  }
}
