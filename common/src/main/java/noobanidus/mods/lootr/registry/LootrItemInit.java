package noobanidus.mods.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import noobanidus.mods.lootr.api.LootrAPI;

public class LootrItemInit {
    public static final BlockItem CHEST = new BlockItem(LootrBlockInit.CHEST.get(), new Item.Properties());
    public static final BlockItem BARREL = new BlockItem(LootrBlockInit.BARREL.get(), new Item.Properties());
    public static final BlockItem TRAPPED_CHEST = new BlockItem(LootrBlockInit.TRAPPED_CHEST.get(), new Item.Properties());
    public static final BlockItem SHULKER = new BlockItem(LootrBlockInit.SHULKER.get(), new Item.Properties());
    public static final BlockItem INVENTORY = new BlockItem(LootrBlockInit.INVENTORY.get(), new Item.Properties());

    public static final BlockItem TROPHY = new BlockItem(LootrBlockInit.TROPHY.get(), new Item.Properties().rarity(Rarity.EPIC));

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_chest"), CHEST);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_barrel"), BARREL);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_trapped_chest"), TRAPPED_CHEST);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_shulker"), SHULKER);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_inventory"), INVENTORY);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "trophy"), TROPHY);
        //Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "crown"), CROWN);
    }
}
