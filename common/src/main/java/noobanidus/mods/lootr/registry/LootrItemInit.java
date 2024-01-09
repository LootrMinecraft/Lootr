package noobanidus.mods.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class LootrItemInit {
    private static BlockItem chestItem;
    public static final Supplier<BlockItem> CHEST_ITEM_PROVIDER = () -> chestItem;
    private static BlockItem trappedChestItem;
    public static final Supplier<BlockItem> TRAPPED_CHEST_ITEM_PROVIDER = () -> trappedChestItem;
    private static BlockItem barrelItem;
    public static final Supplier<BlockItem> BARREL_ITEM_PROVIDER = () -> barrelItem;
    private static BlockItem shulkerItem;
    public static final Supplier<BlockItem> SHULKER_ITEM_PROVIDER = () -> shulkerItem;
    private static BlockItem inventoryItem;
    public static final Supplier<BlockItem> INVENTORY_ITEM_PROVIDER = () -> inventoryItem;

    private static BlockItem trophyItem;
    public static final Supplier<BlockItem> TROPHY_ITEM_PROVIDER = () -> trophyItem;

    public static void registerItems() {
        chestItem = new BlockItem(LootrBlockInit.CHEST.get(), new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_chest"), chestItem);
        trappedChestItem = new BlockItem(LootrBlockInit.TRAPPED_CHEST.get(), new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_trapped_chest"), trappedChestItem);
        barrelItem = new BlockItem(LootrBlockInit.BARREL.get(), new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_barrel"), barrelItem);
        shulkerItem = new BlockItem(LootrBlockInit.SHULKER.get(), new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_shulker"), shulkerItem);
        inventoryItem = new BlockItem(LootrBlockInit.INVENTORY.get(), new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_inventory"), inventoryItem);
        trophyItem = new BlockItem(LootrBlockInit.TROPHY.get(), new Item.Properties().rarity(Rarity.EPIC));
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootrAPI.MODID, "trophy"), trophyItem);
        //Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "crown"), CROWN);
    }
}
