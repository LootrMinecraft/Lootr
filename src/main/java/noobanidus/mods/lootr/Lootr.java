package noobanidus.mods.lootr;

import net.fabricmc.api.ModInitializer;
import net.zestyblaze.lootr.api.LootrAPI;
import noobanidus.mods.lootr.impl.LootrAPIImpl;
import noobanidus.mods.lootr.registry.*;

public class Lootr implements ModInitializer {
  @Override
  public void onInitialize() {
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
