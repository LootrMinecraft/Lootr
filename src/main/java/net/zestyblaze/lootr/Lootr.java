package net.zestyblaze.lootr;

import net.fabricmc.api.ModInitializer;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.command.CommandLootr;
import net.zestyblaze.lootr.config.LootrConfigInit;
import net.zestyblaze.lootr.event.LootrEventsInit;
import net.zestyblaze.lootr.impl.LootrAPIImpl;
import net.zestyblaze.lootr.init.*;

public class Lootr implements ModInitializer {
  @Override
  public void onInitialize() {
    LootrConfigInit.registerConfig();
    ModItems.registerItems();
    ModBlocks.registerBlocks();
    ModTabs.registerTabs();
    ModBlockEntities.registerBlockEntities();
    ModEntities.registerEntities();
    ModLoot.registerLoot();
    LootrEventsInit.registerEvents();
    ModStats.registerStats();
    ModAdvancements.registerAdvancements();
    CommandLootr.registerCommands();

    LootrAPI.INSTANCE = new LootrAPIImpl();
  }
}
