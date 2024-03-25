package noobanidus.mods.lootr;

import net.fabricmc.api.ModInitializer;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.command.CommandLootr;
import noobanidus.mods.lootr.config.LootrConfigInit;
import noobanidus.mods.lootr.event.LootrEventsInit;
import noobanidus.mods.lootr.impl.LootrAPIImpl;
import net.mods.lootr.init.*;
import noobanidus.mods.lootr.init.ModAdvancements;
import noobanidus.mods.lootr.init.ModBlockEntities;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.init.ModItems;
import noobanidus.mods.lootr.init.ModLoot;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.init.ModTabs;

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
