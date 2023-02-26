package net.zestyblaze.lootr.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.entity.LootrChestMinecartEntity;

public class LootrEntityInit {
    public static EntityType<LootrChestMinecartEntity> LOOTR_MINECART_ENTITY;

    public static void registerEntities() {
        LOOTR_MINECART_ENTITY = Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "lootr_minecart"), FabricEntityTypeBuilder.create(MobCategory.MISC, new EntityType.EntityFactory<LootrChestMinecartEntity>() {
            @Override
            public LootrChestMinecartEntity create(EntityType<LootrChestMinecartEntity> entityType, Level level) {
                return new LootrChestMinecartEntity(entityType, level);
            }
        }).dimensions(EntityDimensions.fixed(0.9f, 1.4f)).trackRangeBlocks(8).build());

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Common Registry - Entities Registered");
        }
    }
}
