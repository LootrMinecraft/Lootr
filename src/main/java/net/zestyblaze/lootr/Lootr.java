package net.zestyblaze.lootr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.impl.LootrAPIImpl;
import net.zestyblaze.lootr.registry.*;

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
