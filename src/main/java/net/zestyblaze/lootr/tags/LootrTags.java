package net.zestyblaze.lootr.tags;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.zestyblaze.lootr.api.LootrAPI;

public class LootrTags {
    public static class Blocks extends LootrTags {
        public static final Tag<Block> CHESTS = tag("chests");
        public static final Tag<Block> TRAPPED_CHESTS = tag("trapped_chests");
        public static final Tag<Block> SHULKERS = tag("shulkers");
        public static final Tag<Block> BARRELS = tag("barrels");
        public static final Tag<Block> CONTAINERS = tag("containers");

        static Tag<Block> tag(String name) {
            return TagFactory.BLOCK.create(new ResourceLocation(LootrAPI.MODID, name));
        }
    }

    public static class Items extends LootrTags {
        public static final Tag<Item> CHESTS = tag("chests");
        public static final Tag<Item> TRAPPED_CHESTS = tag("trapped_chests");
        public static final Tag<Item> SHULKERS = tag("shulkers");
        public static final Tag<Item> BARRELS = tag("barrels");
        public static final Tag<Item> CONTAINERS = tag("containers");

        static Tag<Item> tag(String name) {
            return TagFactory.ITEM.create(new ResourceLocation(LootrAPI.MODID, name));
        }
    }
}
