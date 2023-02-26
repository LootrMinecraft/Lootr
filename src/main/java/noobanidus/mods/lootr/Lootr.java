package noobanidus.mods.lootr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.LootrModConfig;
import noobanidus.mods.lootr.init.*;

public class Lootr implements ModInitializer {
  public static CreativeModeTab TAB = FabricItemGroupBuilder.build(new ResourceLocation(LootrAPI.MODID, LootrAPI.MODID), () -> new ItemStack(LootrItemInit.CHEST));

  @Override
  public void onInitialize() {
    LootrAPI.LOG.info("Lootr is installed, loading now! Thanks for installing! <3");
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

    if (LootrModConfig.get().debug.debugMode) {
      LootrAPI.LOG.info("Lootr: Registry - Mod Fully Loaded!");
    }
  }
}
