package noobanidus.mods.lootr.common.api.config;

import java.util.Locale;

public enum SaveMode {
  ALWAYS,
  SMART,
  WHEN_OPENED;

  public static final String ATERNOS = "ATERNOS_SERVER_ID";
  public static final String EXAROTON = "EXAROTON_SERVER_ID";

  public static SaveMode fromString(String name) {
    for (SaveMode mode : values()) {
      if (mode.name().toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT))) {
        return mode;
      }
    }
    return SMART;
  }

  public String toString () {
    return this.name().toLowerCase(Locale.ROOT);
  }

  public boolean shouldInitialSave () {
    if (this == ALWAYS) {
      return true;
    } else if (this == SMART) {
      return System.getenv(ATERNOS) == null && System.getenv(EXAROTON) == null;
    } else {
      return false;
    }
  }
}
