package net.zestyblaze.lootr.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.zestyblaze.lootr.Lootr;
import net.zestyblaze.lootr.config.LootrModConfig;

public class LootrItemInit {
    public static void loadItems() {
        Registry.register(Registry.ITEM, new Identifier(Lootr.MODID, "lootr_chest"), new BlockItem(LootrBlockInit.CHEST, new FabricItemSettings().group(ItemGroup.MISC)));

        if(LootrModConfig.get().debugMode) {
            Lootr.LOGGER.info("Lootr: Registry - Items Loaded!");
        }
    }
}
