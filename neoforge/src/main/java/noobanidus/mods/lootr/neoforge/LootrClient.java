package noobanidus.mods.lootr.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import noobanidus.mods.lootr.common.api.LootrAPI;

@Mod(value= LootrAPI.MODID, dist= Dist.CLIENT)
public class LootrClient {
  public LootrClient(ModContainer modContainer, IEventBus modBus) {
    modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
  }
}
