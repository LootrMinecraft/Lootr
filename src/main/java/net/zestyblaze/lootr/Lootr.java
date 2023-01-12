package net.zestyblaze.lootr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.registry.*;

public class Lootr implements ModInitializer {
  private static final CreativeModeTab TAB = FabricItemGroup.builder(new ResourceLocation(LootrAPI.MODID, LootrAPI.MODID)).title(Component.translatable("itemGroup.lootr.lootr")).icon(() -> new ItemStack(LootrItemInit.CHEST)).build();

  @Override
  public void onInitialize() {
    LootrConfigInit.registerConfig();
    LootrItemInit.registerItems();
    LootrBlockInit.registerBlocks();
    LootrBlockEntityInit.registerBlockEntities();
    LootrEntityInit.registerEntities();
    LootrLootInit.registerLoot();
    LootrEventsInit.registerEvents();
    LootrStatsInit.registerStats();
    LootrAdvancementsInit.registerAdvancements();
    LootrCommandInit.registerCommands();

    ItemGroupEvents.modifyEntriesEvent(TAB).register(content -> content.accept(LootrItemInit.TROPHY));
  }
}
