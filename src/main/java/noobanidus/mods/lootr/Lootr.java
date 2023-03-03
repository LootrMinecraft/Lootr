package noobanidus.mods.lootr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.init.*;

public class Lootr implements ModInitializer {
  public static CreativeModeTab TAB = FabricItemGroupBuilder.build(new ResourceLocation(LootrAPI.MODID, LootrAPI.MODID), () -> new ItemStack(ModItems.CHEST));

  @Override
  public void onInitialize() {
    LootrConfigInit.registerConfig();
    ModItems.registerItems();
    ModBlocks.registerBlocks();
    ModBlockEntities.registerBlockEntities();
    ModEntities.registerEntities();
    ModLoot.registerLoot();
    ModEvents.registerEvents();
    ModStats.register();
    ModAdvancements.registerAdvancements();
    LootrCommandInit.registerCommands();
  }
}
