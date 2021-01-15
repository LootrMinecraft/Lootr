package noobanidus.mods.lootr.setup;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.lootr.init.ModMisc;

public class CommonSetup {
  public static void init(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      ModMisc.register();
    });
  }
}
