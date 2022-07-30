package noobanidus.mods.lootr;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

@SuppressWarnings("unused")
public class LootrLateCore implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        ImmutableList.Builder<String> list = ImmutableList.builder();
        if(Loader.isModLoaded("dimdoors"))
            list.add("lootr.mixins.dimdoors.json");
        return list.build();
    }
}
