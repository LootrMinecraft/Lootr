package noobanidus.mods.lootr.neoforge.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import noobanidus.mods.lootr.common.data.DataStorage;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleTick {
  @SubscribeEvent
  public static void onServerTick (ServerTickEvent.Post event) {
    DataStorage.doTick();

    BlockEntityTicker.onServerTick(event.getServer());
  }
}
