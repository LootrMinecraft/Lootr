package net.zestyblaze.lootr;

import net.fabricmc.api.ModInitializer;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.registry.LootrConfigInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Lootr implements ModInitializer {
	@Override
	public void onInitialize() {
		LootrAPI.LOG.info("Lootr is installed, loading now! Thanks for installing! <3");
		LootrConfigInit.registerConfig();

		if(LootrModConfig.get().debug.debugMode) {
			LootrAPI.LOG.info("Lootr: Registry - Mod Fully Loaded!");
		}
	}
}
