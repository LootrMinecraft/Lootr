package noobanidus.mods.lootr.common.data;

import net.minecraft.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.SavedDataStorage;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinMinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * One-off, eager import of the pre-26 per-container files into the section-based storage.
 * <br />
 * Runs when the server has started, guarded by a flag file: if {@link #FLAG_FILE} exists nothing is done.
 * Every legacy file that is converted is renamed to {@code .migrated}, so a run that was interrupted (or
 * a manual re-run after deleting the flag) never imports the same container twice. The flag is only written
 * after a pass in which every file was handled.
 */
public final class LegacyMigrationRunner {
  public static final String FLAG_FILE = "lootr-version-migration-to-26.3.flag";
  private static final String MIGRATED_SUFFIX = ".migrated";
  private static final int BATCH_SIZE = 2000;

  private LegacyMigrationRunner() {
  }

  /**
   * The outcome of a pass.
   *
   * @param found     legacy container files found
   * @param migrated  containers imported
   * @param skipped   containers that already had data in the new storage (the legacy file is still renamed)
   * @param failed    files that could not be converted and were left in place
   * @param skippedByFlag true if the flag file already existed and nothing was done
   */
  public record Result(int found, int migrated, int skipped, int failed, boolean skippedByFlag) {
    public boolean complete() {
      return failed == 0;
    }
  }

  /**
   * Called on server start. Does nothing if the flag file exists.
   */
  public static void runOnStartup(MinecraftServer server) {
    if (LootrAPI.isDisabled()) {
      // Lootr is disabled by configuration; `/lootr migrate` can be used later.
      return;
    }
    run(server, false);
  }

  /**
   * @param ignoreFlag true to scan even if the flag file exists (used by {@code /lootr migrate}). Files already
   *                   renamed to {@code .migrated} are never read again either way.
   */
  public static Result run(MinecraftServer server, boolean ignoreFlag) {
    Path root = LegacyDataMigrator.getLegacyRoot(server);
    Path flag = root.resolve(FLAG_FILE);
    if (!ignoreFlag && Files.exists(flag)) {
      return new Result(0, 0, 0, 0, true);
    }

    SavedDataStorage storage = DataStorage.getDataStorage();
    if (storage == null) {
      LootrAPI.LOG.error("Lootr cannot migrate legacy data: saved data storage is not available.");
      return new Result(0, 0, 0, 1, false);
    }

    List<Path> files = findLegacyFiles(root);
    if (files.isEmpty()) {
      LootrAPI.LOG.info("No legacy Lootr container files to migrate; writing the migration flag.");
      writeFlag(flag, 0);
      return new Result(0, 0, 0, 0, false);
    }

    LootrAPI.LOG.info("Migrating {} legacy Lootr container files to the 26.3 data format.", files.size());
    LegacyDataMigrator.LegacyTimers timers = LegacyDataMigrator.loadTimers(server);

    List<Path> converted = new ArrayList<>(files.size());
    int migrated = 0, skipped = 0, failed = 0, processed = 0;
    // Converting is pure (file read plus data fixers), so each batch is converted in parallel and then imported on
    // this thread, as saved data is not thread safe.
    for (int start = 0; start < files.size(); start += BATCH_SIZE) {
      List<Path> batch = files.subList(start, Math.min(files.size(), start + BATCH_SIZE));
      List<Optional<LegacyDataMigrator.ConvertedContainer>> results = batch.parallelStream()
          .map(file -> LegacyDataMigrator.convert(file, server.registryAccess(), timers))
          .toList();
      for (int i = 0; i < batch.size(); i++) {
        Path file = batch.get(i);
        var result = results.get(i);
        if (result.isEmpty()) {
          failed++;
          continue;
        }
        LegacyDataMigrator.ConvertedContainer container = result.get();
        Section section = storage.computeIfAbsent(DataStorage.getSectionSavedData(container.sectionId()));
        // Never overwrite data that already exists in the new storage.
        if (section.addStore(container.store())) {
          migrated++;
        } else {
          skipped++;
          LootrAPI.LOG.warn("Container {} already has data in the new storage; keeping it and ignoring {}.", container.id(), file);
        }
        converted.add(file);
      }
      processed += batch.size();
      LootrAPI.LOG.info("Lootr migration progress: {} / {} files.", processed, files.size());
      // This all happens inside a single server tick; without this the watchdog would treat a long migration as a hang.
      ((AccessorMixinMinecraftServer) server).Lootr$setNextTickTimeNanos(Util.getNanos());
    }

    // Persist the new data before touching the old files, so a crash cannot lose both.
    storage.saveAndJoin();
    ((AccessorMixinMinecraftServer) server).Lootr$setNextTickTimeNanos(Util.getNanos());

    int renamed = 0;
    for (Path file : converted) {
      if (++renamed % BATCH_SIZE == 0) {
        ((AccessorMixinMinecraftServer) server).Lootr$setNextTickTimeNanos(Util.getNanos());
      }
      try {
        Files.move(file, file.resolveSibling(file.getFileName() + MIGRATED_SUFFIX), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        LootrAPI.LOG.error("Migrated {} but could not rename it; it will be skipped as already present on the next run.", file, e);
      }
    }

    Result result = new Result(files.size(), migrated, skipped, failed, false);
    if (result.complete()) {
      writeFlag(flag, migrated);
      LootrAPI.LOG.info("Lootr migration finished: {} containers migrated, {} already present.", migrated, skipped);
    } else {
      LootrAPI.LOG.error("Lootr migration finished with {} failed files (left in place): {} migrated, {} already present. The flag file was not written, so these will be retried on the next start.", failed, migrated, skipped);
    }
    return result;
  }

  /**
   * Legacy files live at {@code data/lootr/<d>/<dd>/<uuid>.dat}. The {@code ticking} directory has the same depth
   * but holds timers, and is excluded.
   */
  private static List<Path> findLegacyFiles(Path root) {
    if (!Files.isDirectory(root)) {
      return List.of();
    }
    try (Stream<Path> stream = Files.walk(root, 3)) {
      return stream.filter(Files::isRegularFile)
          .filter(p -> root.relativize(p).getNameCount() == 3)
          .filter(p -> !root.relativize(p).getName(0).toString().equals("ticking"))
          .filter(p -> isUuidDatFile(p.getFileName().toString()))
          .sorted()
          .toList();
    } catch (IOException e) {
      LootrAPI.LOG.error("Failed to scan {} for legacy Lootr data.", root, e);
      return List.of();
    }
  }

  private static boolean isUuidDatFile(String name) {
    if (!name.endsWith(".dat")) {
      return false;
    }
    try {
      UUID.fromString(name.substring(0, name.length() - 4));
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private static void writeFlag(Path flag, int migrated) {
    try {
      Files.createDirectories(flag.getParent());
      Files.writeString(flag, "Lootr migrated " + migrated + " containers to the 26.3 data format.\nDelete this file to run the migration again; already migrated files (*.migrated) are never imported twice.\n");
    } catch (IOException e) {
      LootrAPI.LOG.error("Failed to write the Lootr migration flag file {}.", flag, e);
    }
  }
}
