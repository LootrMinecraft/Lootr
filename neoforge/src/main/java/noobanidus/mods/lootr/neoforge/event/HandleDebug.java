package noobanidus.mods.lootr.neoforge.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.debug.TagChecker;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleDebug {

  @SubscribeEvent
  public static void handleTagUpdate(TagsUpdatedEvent event) {
    TagChecker.checkTags();
  }
}
