package noobanidus.mods.lootr;

import net.fabricmc.api.ModInitializer;

public class LootrFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Lootr.init();
    }
}
