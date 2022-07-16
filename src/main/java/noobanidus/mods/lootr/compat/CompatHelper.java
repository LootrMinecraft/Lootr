package noobanidus.mods.lootr.compat;

import net.minecraftforge.fml.common.Loader;

public class CompatHelper {
    public static boolean isRecComplexLoaded() {
        return Loader.isModLoaded("reccomplex");
    }
}
