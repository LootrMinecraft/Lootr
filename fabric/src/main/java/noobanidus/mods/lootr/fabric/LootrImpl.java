package noobanidus.mods.lootr.fabric;

import net.fabricmc.api.ModInitializer;
import noobanidus.mods.lootr.Lootr;

public class LootrImpl implements ModInitializer {
    @Override
    public void onInitialize() {
        Lootr.init();
    }
}
