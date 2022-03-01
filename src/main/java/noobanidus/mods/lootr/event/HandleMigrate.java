package noobanidus.mods.lootr.event;

import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.data.DataStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleMigrate {
  private static void append(StringBuilder builder, int intVal) {
    append(builder, intVal, true);
  }

  private static void append(StringBuilder builder, int intVal, boolean sep) {
    if (intVal < 10) {
      builder.append('0').append(intVal);
    } else {
      builder.append(intVal);
    }
    if (sep) {
      builder.append('-');
    }
  }

  @SubscribeEvent
  public static void onServerStarting(ServerStartedEvent event) {
    List<Path> toMigrate;

    Path data = event.getServer().getWorldPath(new LevelResource("data"));
    Path dataLootr = data.resolve("lootr");

    try {
      toMigrate = Files.walk(event.getServer().getWorldPath(new LevelResource("data")), 1).collect(Collectors.toList());
    } catch (IOException e) {
      LootrAPI.LOG.error("Unable to begin migration of existing data!", e);
      return;
    }

    toMigrate.removeIf(path -> !path.getFileName().toString().startsWith("Lootr-"));

    if (!toMigrate.isEmpty()) {
      LootrAPI.LOG.info("Backing up {} files...", toMigrate.size());
      Calendar time = Calendar.getInstance();
      StringBuilder filenameBuilder = new StringBuilder();
      filenameBuilder.append("LootrMigrationBackup-");
      append(filenameBuilder, time.get(Calendar.YEAR));
      append(filenameBuilder, time.get(Calendar.MONTH) + 1);
      append(filenameBuilder, time.get(Calendar.DAY_OF_MONTH));
      append(filenameBuilder, time.get(Calendar.HOUR_OF_DAY));
      append(filenameBuilder, time.get(Calendar.MINUTE));
      append(filenameBuilder, time.get(Calendar.SECOND), false);
      String name = filenameBuilder.toString();
      Path backupName = data.resolve(name + ".zip");
      File backup = backupName.toFile();
      int inc = 0;
      while (backup.exists()) {
        backupName = data.resolve(name + "-" + inc + ".zip");
        backup = backupName.toFile();
        if (inc++ >= 99) {
          throw new IllegalStateException("Unable to create backup for Lootr data files. Reached '" + name + "-" + inc + ".zip' and all files exist!");
        }
      }

      try {
        backup.getParentFile().mkdirs();
        if (!backup.createNewFile()) {
          throw new IllegalStateException("Unable to create backup for Lootr data files! Couldn't create " + backup);
        }
      } catch (IOException e) {
        throw new IllegalStateException("Unable to create backup for Lootr data files!", e);
      }

      ZipOutputStream backupZip;
      try {
        backupZip = new ZipOutputStream(new FileOutputStream(backup));
      } catch (FileNotFoundException e) {
        throw new IllegalStateException("Unable to create backup for Lootr data files!", e);
      }

      backupZip.setLevel(0);
      boolean failure = false;
      byte[] readBuffer = new byte[4096];
      for (Path path : toMigrate) {
        ZipEntry entry = new ZipEntry(path.getFileName().toString());
        try {
          backupZip.putNextEntry(entry);
          FileInputStream inputStream = new FileInputStream(path.toFile());
          int len;
          while ((len = inputStream.read(readBuffer)) > 0) {
            backupZip.write(readBuffer, 0, len);
          }
          backupZip.closeEntry();
          inputStream.close();
        } catch (IOException e) {
          failure = true;
          LootrAPI.LOG.error("Unable to fully back-up Lootr data, failure while reading: {}", path, e);
        }
      }

      try {
        backupZip.close();
      } catch (IOException e) {
        throw new IllegalStateException("Unable to close current back-up file: " + backup, e);
      }

      if (failure) {
        throw new IllegalStateException("Unable to fully back-up Lootr data. Please check log file for details as to which file failed to back-up.");
      } else {
        LootrAPI.LOG.info("Completed backup! {} files were backed up to {}", toMigrate.size(), backup);
      }
    }

    Map<Path, Path> migrations = new HashMap<>();

    for (Path path : toMigrate) {
      String fileName = path.getFileName().toString();

      if (fileName.startsWith(DataStorage.ID_OLD) || fileName.startsWith(DataStorage.SCORED_OLD) || fileName.startsWith(DataStorage.DECAY_OLD) || fileName.startsWith(DataStorage.REFRESH_OLD)) {
        // Data files go into the subdirectory
        migrations.put(path, dataLootr.resolve(path.getFileName()));
      } else {
        // Determine file ID
        String uuid;
        if (fileName.startsWith("Lootr-chests") || fileName.startsWith("Lootr-custom")) {
          uuid = fileName.split("-", 4)[3];
        } else if (fileName.startsWith("Lootr-entity")) {
          uuid = fileName.split("-", 3)[2];
        } else {
          LootrAPI.LOG.error("Invalid file name found while traversing data. Could not migrate: '" + path + "'");
          continue;
        }
        String containerId = uuid.substring(0, 2);
        try {
          Files.createDirectories(dataLootr.resolve(uuid.substring(0, 1)).resolve(containerId));
        } catch (IOException e) {
          LootrAPI.LOG.error("Unable to create 'lootr/" + containerId + "' subdirectory. Could not migrate: '" + path + "'", e);
          continue;
        }
        migrations.put(path, dataLootr.resolve(uuid.substring(0, 1)).resolve(containerId).resolve(uuid));
      }
    }

    if (!migrations.isEmpty()) {
      LootrAPI.LOG.info("Migrating Lootr data files to subdirectory...");
      for (Map.Entry<Path, Path> migrationEntry : migrations.entrySet()) {
        try {
          Files.move(migrationEntry.getKey(), migrationEntry.getValue());
        } catch (IOException e) {
          LootrAPI.LOG.error("Unable to migrate from '" + migrationEntry.getKey() + "' to '" + migrationEntry.getValue() + "'", e);
        }
      }
      LootrAPI.LOG.info("Migrated " + migrations.size() + " Lootr data files to subdirectory!");
    }
  }
}
