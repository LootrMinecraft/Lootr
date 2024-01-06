package noobanidus.mods.lootr;

import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.impl.LootrAPIImpl;
import noobanidus.mods.lootr.registry.*;

public class Lootr {
  public static void init() {
    LootrConfigInit.registerConfig();
    LootrItemInit.registerItems();
    LootrBlockInit.registerBlocks();
    LootrTabInit.registerTabs();
    LootrBlockEntityInit.registerBlockEntities();
    LootrEntityInit.registerEntities();
    LootrLootInit.registerLoot();
    LootrEventsInit.registerEvents();
    LootrStatsInit.registerStats();
    LootrAdvancementsInit.registerAdvancements();
    LootrCommandInit.registerCommands();

    LootrAPI.INSTANCE = new LootrAPIImpl();
  }
}
