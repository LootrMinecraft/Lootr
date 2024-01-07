package noobanidus.mods.lootr.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Contract;

@Environment(EnvType.CLIENT)
public class LootrClient {

    @Contract
    @ExpectPlatform
    public static void initModels() {
        throw new UnsupportedOperationException();
    }
}
