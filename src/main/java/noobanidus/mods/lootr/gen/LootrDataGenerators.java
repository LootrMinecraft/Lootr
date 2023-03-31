package noobanidus.mods.lootr.gen;

import net.minecraft.core.HolderLookup;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid= LootrAPI.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class LootrDataGenerators {
  @SubscribeEvent
  public static void gatherData (GatherDataEvent event) {
    if (event.includeServer()) {
      ExistingFileHelper helper = event.getExistingFileHelper();
      LootrBlockTagProvider blocks;
      event.getGenerator().addProvider(true, blocks = new LootrBlockTagProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), LootrAPI.MODID, helper));
      event.getGenerator().addProvider(true, new LootrItemTagsProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), blocks.contentsGetter(), LootrAPI.MODID, helper));
    }
  }
}
