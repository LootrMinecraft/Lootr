package noobanidus.mods.lootr.registry;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.world.InteractionResult;
import noobanidus.mods.lootr.config.LootrModConfig;

public class LootrConfigInit {
  public static void registerConfig() {
    AutoConfig.register(LootrModConfig.class, GsonConfigSerializer::new);
    ConfigHolder<LootrModConfig> config = AutoConfig.getConfigHolder(LootrModConfig.class);
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
