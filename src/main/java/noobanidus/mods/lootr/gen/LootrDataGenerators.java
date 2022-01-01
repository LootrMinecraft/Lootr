package noobanidus.mods.lootr.gen;


import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import noobanidus.mods.lootr.Lootr;

@Mod.EventBusSubscriber(modid= Lootr.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class LootrDataGenerators {
  @SubscribeEvent
  public static void gatherData (GatherDataEvent event) {
    if (event.includeServer()) {
      ExistingFileHelper helper = event.getExistingFileHelper();
      LootrBlockTagsProvider blocks = new LootrBlockTagsProvider(event.getGenerator(), Lootr.MODID, helper);
      event.getGenerator().addProvider(blocks);
      event.getGenerator().addProvider(new LootrItemTagsProvider(event.getGenerator(), blocks, Lootr.MODID, helper));
    }
  }
}
