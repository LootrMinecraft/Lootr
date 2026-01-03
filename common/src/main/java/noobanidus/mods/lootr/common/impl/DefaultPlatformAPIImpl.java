package noobanidus.mods.lootr.common.impl;

import noobanidus.mods.lootr.common.api.IPlatformAPI;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.config.SaveMode;

import java.util.Set;

public abstract class DefaultPlatformAPIImpl implements IPlatformAPI {
  public static final Set<String> SERVER_ENVIRONMENT_REDUCE_FILES = Set.of(
      "ATERNOS_SERVER_ID",
      "EXAROTON_SERVER_ID",
      "LOOTR_REDUCE_DATA_FILE_QUANTITY"
  );

  private final boolean doesServerNeedLessFiles;

  public DefaultPlatformAPIImpl() {
    boolean serverNeedsLessFiles = false;

    for (String name : SERVER_ENVIRONMENT_REDUCE_FILES) {
      if (System.getenv(name) != null) {
        LootrAPI.LOG.info("Environment variable '{}' detected. If the save mode configuration is set to 'SMART', Lootr will only save data files for containers that have been opened by players, rather than every file.", name);
        serverNeedsLessFiles = true;
        break;
      }
    }

    this.doesServerNeedLessFiles = serverNeedsLessFiles;
  }

  @Override
  public boolean shouldDoInitialSave() {
    SaveMode mode = LootrAPI.getFileSaveMode();
    if (mode == SaveMode.ALWAYS) {
      return true;
    } else if (mode == SaveMode.SMART) {
      return !doesServerNeedLessFiles;
    } else {
      return false;
    }
  }
}
