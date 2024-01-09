package noobanidus.mods.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.function.Supplier;

public class LootrTabInit {
    private static CreativeModeTab lootrTab;
    public static final Supplier<CreativeModeTab> LOOTR_TAB_PROVIDER = () -> lootrTab;

    public static void registerTabs() {
        lootrTab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(Component.translatable("itemGroup.lootr.lootr"))
                .icon(() -> new ItemStack(LootrBlockInit.TROPHY.get()))
                .displayItems((p, output) -> {
                    output.accept(LootrBlockInit.TROPHY.get());
                }).build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(LootrAPI.MODID, "lootr"), lootrTab);
    }
}
