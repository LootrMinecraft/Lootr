package noobanidus.mods.lootr.gen;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import noobanidus.mods.lootr.Lootr;

@Mod.EventBusSubscriber(modid= Lootr.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class LootrDataGenerators {
  @SubscribeEvent
  public static void gatherData (GatherDataEvent event) {
    if (event.includeServer()) {
      ExistingFileHelper helper = event.getExistingFileHelper();
      event.getGenerator().addProvider(new LootrBlockTagGenerator(event.getGenerator(), Lootr.MODID, helper));
    }
  }
}
