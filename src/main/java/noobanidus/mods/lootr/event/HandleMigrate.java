package noobanidus.mods.lootr.event;

import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.data.DataStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class HandleMigrate {
  @SubscribeEvent
  public static void onServerStarting(ServerStartedEvent event) {
    Map<Path, Path> migrations = new HashMap<>();
    List<Path> toMigrate;

    Path data = event.getServer().getWorldPath(new LevelResource("data"));
    Path dataLootr = data.resolve("lootr");

    try {
      toMigrate = Files.walk(event.getServer().getWorldPath(new LevelResource("data")), 1).collect(Collectors.toList());
    } catch (IOException e) {
      Lootr.LOG.error("Unable to begin migration of existing data!", e);
      return;
    }

    for (Path path : toMigrate) {
      String fileName = path.getFileName().toString();
      if (!fileName.startsWith("Lootr-")) {
        continue;
      }

      if (fileName.startsWith(DataStorage.ID_OLD) || fileName.startsWith(DataStorage.SCORED_OLD) || fileName.startsWith(DataStorage.DECAY_OLD) || fileName.startsWith(DataStorage.REFRESH_OLD)) {
        // Data files go into the subdirectory
        migrations.put(path, dataLootr.resolve(path.getFileName()));
      } else {
        // Determine file ID
        String containerId;
        if (fileName.startsWith("Lootr-chests") || fileName.startsWith("Lootr-custom")) {
          containerId = fileName.split("-", 4)[3].substring(0, 2);
        } else if (fileName.startsWith("Lootr-entity")) {
          containerId = fileName.split("-", 3)[2].substring(0, 2);
        } else {
          Lootr.LOG.error("Invalid file name found while traversing data. Could not migrate: '" + path + "'");
          continue;
        }
        try {
          Files.createDirectories(dataLootr.resolve(containerId));
        } catch (IOException e) {
          Lootr.LOG.error("Unable to create 'lootr/" + containerId + "' subdirectory. Could not migrate: '" + path + "'", e);
          continue;
        }
        migrations.put(path, dataLootr.resolve(containerId).resolve(path.getFileName()));
      }
    }

    if (!migrations.isEmpty()) {
      Lootr.LOG.info("Migrating Lootr data files to subdirectory...");
      for (Map.Entry<Path, Path> migrationEntry : migrations.entrySet()) {
        try {
          Files.move(migrationEntry.getKey(), migrationEntry.getValue());
        } catch (IOException e) {
          Lootr.LOG.error("Unable to migrate from '" + migrationEntry.getKey() + "' to '" + migrationEntry.getValue() + "'", e);
        }
      }
      Lootr.LOG.info("Migrated " + migrations.size() + " Lootr data files to subdirectory!");
    }
  }
}
