package net.zestyblaze.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.blocks.LootrChestBlock;

public class LootrBlockInit {
    public static LootrChestBlock CHEST = new LootrChestBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).strength(2.5f));

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_chest"), CHEST);
    }
}
