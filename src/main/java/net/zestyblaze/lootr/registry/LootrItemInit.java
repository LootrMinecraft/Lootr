package net.zestyblaze.lootr.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.zestyblaze.lootr.Lootr;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.item.CrownItem;

public class LootrItemInit {
    public static final BlockItem CHEST = new BlockItem(LootrBlockInit.CHEST, new Item.Properties());
    public static final BlockItem TRAPPED_CHEST = new BlockItem(LootrBlockInit.TRAPPED_CHEST, new Item.Properties());
    public static final BlockItem SHULKER = new BlockItem(LootrBlockInit.SHULKER, new Item.Properties());

    public static final BlockItem TROPHY = new BlockItem(LootrBlockInit.TROPHY, new FabricItemSettings().rarity(Rarity.EPIC).group(Lootr.TAB));
    public static final CrownItem CROWN = new CrownItem(new FabricItemSettings().rarity(Rarity.EPIC).group(Lootr.TAB));

    public static void registerItems() {
        Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_chest"), CHEST);
        Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_trapped_chest"), TRAPPED_CHEST);
        Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_shulker"), SHULKER);
        Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "trophy"), TROPHY);
        //Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "crown"), CROWN);

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Common Registry - Items Registered");
        }
    }
}
