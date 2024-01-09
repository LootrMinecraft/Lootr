package noobanidus.mods.lootr.forge;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.util.forge.PlatformUtilsImpl;

@Mod("lootr")
public class LootrImpl {
    public LootrImpl() {
        Lootr.init();
    }

    @SubscribeEvent
    public void onCommands(RegisterCommandsEvent event) {
        PlatformUtilsImpl.COMMAND_ENTRIES.forEach(callback -> callback.consume(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
    }
}
