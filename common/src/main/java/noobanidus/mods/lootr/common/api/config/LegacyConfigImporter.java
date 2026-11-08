package noobanidus.mods.lootr.common.api.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pre-26 versions of Lootr stored their settings in {@code config/lootr.json}; 26.x uses {@code lootr-common.jsonc}
 * and never reads the old file. Servers that had {@code conversion.disable} set (typically to migrate by hand) would
 * otherwise come back with every disable switched off, so that one setting is carried over, once.
 */
public final class LegacyConfigImporter {
  private static final String LEGACY_FILE = "lootr.json";
  private static final String MARKER_FILE = "lootr-legacy-config-imported.flag";

  private LegacyConfigImporter() {
  }

  /**
   * Imports the old {@code conversion.disable} setting the first time the old file is seen. The old file is left untouched.
   * Call after {@link LootrCommonConfig} has been registered.
   */
  @SuppressWarnings("deprecation")
  public static void importIfNeeded(Path configDirectory) {
    Path legacy = configDirectory.resolve(LEGACY_FILE);
    Path marker = configDirectory.resolve(MARKER_FILE);
    if (Files.exists(marker) || !Files.isRegularFile(legacy)) {
      return;
    }

    boolean disabled;
    try (Reader reader = Files.newBufferedReader(legacy)) {
      JsonElement root = JsonParser.parseReader(reader);
      JsonObject conversion = root.isJsonObject() && root.getAsJsonObject().get("conversion") instanceof JsonObject o ? o : new JsonObject();
      disabled = conversion.has("disable") && conversion.get("disable").isJsonPrimitive() && conversion.get("disable").getAsBoolean();
    } catch (IOException | RuntimeException e) {
      LootrAPI.LOG.error("Unable to read the old Lootr config {}; its settings were not imported.", legacy, e);
      return;
    }

    if (disabled) {
      LootrCommonConfig.Conversion.disableConversion = true;
      LootrCommonConfig.Conversion.disableTicks = true;
      LootrConfig.getConfigurator().saveConfig(LootrCommonConfig.class);
      LootrAPI.LOG.info("The old Lootr config {} has conversion.disable enabled; enabled both disable_conversion and disable_ticks in lootr-common.jsonc.", legacy);
    }

    try {
      Files.writeString(marker, "The old " + LEGACY_FILE + " was checked on first start of the 26.x config.\nDelete this file to have it checked again.\n");
    } catch (IOException e) {
      LootrAPI.LOG.error("Failed to write {}; the old Lootr config will be checked again on the next start.", marker, e);
    }
  }
}
