package noobanidus.mods.lootr.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import noobanidus.mods.lootr.config.ConfigManager;

public class ModConfig {
  public static void register() {
    AutoConfig.register(ConfigManager.class, GsonConfigSerializer::new);
  }
}
