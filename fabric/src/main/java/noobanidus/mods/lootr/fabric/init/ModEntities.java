package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;

public class ModEntities {
  public static EntityType<LootrChestMinecartEntity> LOOTR_MINECART_ENTITY;

  public static void registerEntities() {
    LOOTR_MINECART_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE, LootrProperties.LOOTR_CART, EntityType.Builder.of((EntityType.EntityFactory<LootrChestMinecartEntity>) LootrChestMinecartEntity::new, MobCategory.MISC).sized(0.9f, 1.4f).clientTrackingRange(8).build());
  }
}
