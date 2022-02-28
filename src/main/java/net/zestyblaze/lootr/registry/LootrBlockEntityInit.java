package net.zestyblaze.lootr.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.blocks.entities.LootrChestBlockEntity;
import net.zestyblaze.lootr.config.LootrModConfig;

public class LootrBlockEntityInit {
    public static final BlockEntityType<LootrChestBlockEntity> SPECIAL_LOOT_CHEST = FabricBlockEntityTypeBuilder.create(LootrChestBlockEntity::new, LootrBlockInit.CHEST).build(null);

    public static void registerBlockEntities() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "special_loot_chest"), SPECIAL_LOOT_CHEST);

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Common Registry - BlockEntities Registered");
        }
    }
}
