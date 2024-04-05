package noobanidus.mods.lootr.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.world.InteractionResult;
import noobanidus.mods.lootr.config.ConfigManager;

public class LootrConfigInit {
  public static void registerConfig() {
    AutoConfig.register(ConfigManager.class, GsonConfigSerializer::new);
    ConfigHolder<ConfigManager> config = AutoConfig.getConfigHolder(ConfigManager.class);
    config.registerLoadListener((manager, configData) -> {
      configData.reset();
      return InteractionResult.PASS;
    });
    config.registerSaveListener((manager, configData) -> {
      configData.reset();
      return InteractionResult.PASS;
    });
  }
}
