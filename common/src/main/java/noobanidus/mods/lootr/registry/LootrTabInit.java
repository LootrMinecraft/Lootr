package noobanidus.mods.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.api.LootrAPI;

public class LootrTabInit {
    public static final CreativeModeTab LOOTR_TAB = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.lootr.lootr"))
            .icon(() -> new ItemStack(LootrBlockInit.TROPHY))
            .displayItems((p, output) -> {
                output.accept(LootrBlockInit.TROPHY);
            }).build();

    public static void registerTabs () {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(LootrAPI.MODID, "lootr"), LOOTR_TAB);
    }
}
