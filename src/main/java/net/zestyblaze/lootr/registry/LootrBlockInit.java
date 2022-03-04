package net.zestyblaze.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.blocks.LootrChestBlock;
import net.zestyblaze.lootr.blocks.LootrInventoryBlock;
import net.zestyblaze.lootr.blocks.TrophyBlock;
import net.zestyblaze.lootr.config.LootrModConfig;

public class LootrBlockInit {
    public static final LootrChestBlock CHEST = new LootrChestBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).strength(2.5f));
    public static final LootrInventoryBlock INVENTORY = new LootrInventoryBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD));

    public static final Block TROPHY = new TrophyBlock(BlockBehaviour.Properties.of(Material.METAL).strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15));

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_chest"), CHEST);
        Registry.register(Registry.BLOCK, new ResourceLocation(LootrAPI.MODID, "trophy"), TROPHY);

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Common Registry - Blocks Registered");
        }
    }
}
