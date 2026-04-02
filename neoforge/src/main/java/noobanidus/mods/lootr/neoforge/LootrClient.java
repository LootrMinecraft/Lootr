package noobanidus.mods.lootr.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.config.LootrClientConfig;
import noobanidus.mods.lootr.common.api.config.LootrConfig;

@Mod(value= LootrAPI.MODID, dist= Dist.CLIENT)
public class LootrClient {
  public LootrClient(ModContainer modContainer, IEventBus modBus) {
  }
}
