package noobanidus.mods.lootr.common.api.config;

import java.util.Locale;

public enum SaveMode {
  ALWAYS,
  SMART,
  WHEN_OPENED;



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
}
