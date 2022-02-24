package net.zestyblaze.lootr;

import net.fabricmc.api.ModInitializer;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.registry.LootrAdvancementsInit;
import net.zestyblaze.lootr.registry.LootrBlockInit;
import net.zestyblaze.lootr.registry.LootrConfigInit;
import net.zestyblaze.lootr.registry.LootrItemInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Lootr implements ModInitializer {
	public static final String MODID = "lootr";
	public static final String MODNAME = "Lootr";
	public static final Logger LOGGER = LogManager.getLogger(MODNAME);

	@Override
	public void onInitialize() {
		LOGGER.info("Lootr is installed, loading now! Thanks for installing! <3");
		LootrConfigInit.registerConfig();
		LootrAdvancementsInit.load();
		LootrBlockInit.loadBlocks();
		LootrItemInit.loadItems();

		if(LootrModConfig.get().debugMode) {
			LOGGER.info("Lootr: Registry - Mod Fully Loaded!");
		}
	}
}
