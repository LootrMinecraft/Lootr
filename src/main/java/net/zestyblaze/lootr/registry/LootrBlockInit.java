package net.zestyblaze.lootr.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.zestyblaze.lootr.Lootr;
import net.zestyblaze.lootr.block.LootrChestBlock;
import net.zestyblaze.lootr.config.LootrModConfig;

public class LootrBlockInit {
    public static final Block CHEST = new LootrChestBlock(FabricBlockSettings.copyOf(Blocks.CHEST).strength(2.5f));

    public static void loadBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(Lootr.MODID, "lootr_chest"), CHEST);

        if(LootrModConfig.get().debugMode) {
            Lootr.LOGGER.info("Lootr: Registry - Blocks Loaded!");
        }
    }
}
