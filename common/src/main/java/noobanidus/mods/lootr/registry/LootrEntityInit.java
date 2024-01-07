package noobanidus.mods.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.util.PlatformUtils;

public class LootrEntityInit {
    public static EntityType<LootrChestMinecartEntity> LOOTR_MINECART_ENTITY;

    public static void registerEntities() {
        EntityType<LootrChestMinecartEntity> type = PlatformUtils.createEntityType(MobCategory.MISC, LootrChestMinecartEntity::new, EntityDimensions.fixed(0.9f, 1.4f), 8, "lootr_minecart");
        LOOTR_MINECART_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "lootr_minecart"), type);
    }
}
