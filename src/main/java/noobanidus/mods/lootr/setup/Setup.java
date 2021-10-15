package noobanidus.mods.lootr.setup;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Setup {
  public static void client() {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.addListener(ClientSetup::init);
    modBus.addListener(ClientSetup::stitch);
    modBus.addListener(ClientSetup::modelRegister);
    modBus.addListener(ClientSetup::registerRenderers);
  }
}
