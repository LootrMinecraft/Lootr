package noobanidus.mods.lootr.gen;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import noobanidus.mods.lootr.api.LootrAPI;

@Mod.EventBusSubscriber(modid= LootrAPI.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class LootrDataGenerators {
  @SubscribeEvent
  public static void gatherData (GatherDataEvent event) {
    if (event.includeServer()) {
      ExistingFileHelper helper = event.getExistingFileHelper();
      LootrBlockTagProvider blocks;
      event.getGenerator().addProvider(true, blocks = new LootrBlockTagProvider(event.getGenerator(), LootrAPI.MODID, helper));
      event.getGenerator().addProvider(true, new LootrItemTagsProvider(event.getGenerator(), blocks, LootrAPI.MODID, helper));
    }
  }
}
