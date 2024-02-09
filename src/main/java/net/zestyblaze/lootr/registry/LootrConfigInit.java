package net.zestyblaze.lootr.registry;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.world.InteractionResult;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.config.LootrModConfig;

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
