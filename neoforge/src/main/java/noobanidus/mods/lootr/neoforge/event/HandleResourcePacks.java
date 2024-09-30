package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;

@EventBusSubscriber(modid = LootrAPI.MODID, bus=EventBusSubscriber.Bus.MOD)
public class HandleResourcePacks {
  @SubscribeEvent
  public static void onResourcePacks (AddPackFindersEvent event) {
    if (event.getPackType() == PackType.CLIENT_RESOURCES) {
      event.addPackFinders(
          LootrAPI.rl("resourcepacks/new_textures"),
          PackType.CLIENT_RESOURCES,
          Component.literal("Lootr - New Textures"),
          PackSource.BUILT_IN,
          false,
          Pack.Position.BOTTOM);
    }
  }
}
