package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;

public class ModEntities {
  public static EntityType<LootrChestMinecartEntity> LOOTR_MINECART_ENTITY;
  public static EntityType<LootrItemFrame> ITEM_FRAME;

  public static void registerEntities() {
    LOOTR_MINECART_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE, LootrConstants.LOOTR_CART, EntityType.Builder.of((EntityType.EntityFactory<LootrChestMinecartEntity>) LootrChestMinecartEntity::new, MobCategory.MISC).sized(0.9f, 1.4f).clientTrackingRange(8).build());
    ITEM_FRAME = Registry.register(BuiltInRegistries.ENTITY_TYPE, LootrConstants.ITEM_FRAME, EntityType.Builder.<LootrItemFrame>of(LootrItemFrame::new, MobCategory.MISC)
        .sized(0.5F, 0.5F)
        .eyeHeight(0.0F)
        .clientTrackingRange(10)
        .updateInterval(Integer.MAX_VALUE).build());
  }
}
