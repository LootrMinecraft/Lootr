package noobanidus.mods.lootr.common.api.config;

import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import noobanidus.mods.lootr.common.api.LootrAPI;

public class LootrConfig {
  private static Configurator configurator;

  public static Configurator getConfigurator () {
    if (configurator == null) {
      configurator = new Configurator(LootrAPI.MODID);
    }

    return configurator;
  }
}
