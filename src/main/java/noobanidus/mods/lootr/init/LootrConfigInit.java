package noobanidus.mods.lootr.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import noobanidus.mods.lootr.config.ConfigManager;

public class LootrConfigInit {
  public static void registerConfig() {
    AutoConfig.register(ConfigManager.class, GsonConfigSerializer::new);
  }
}
