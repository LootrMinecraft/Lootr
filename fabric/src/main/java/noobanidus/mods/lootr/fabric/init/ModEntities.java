package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;

public class ModEntities {
  public static EntityType<LootrChestMinecartEntity> MINECART_WITH_CHEST;
  public static EntityType<LootrItemFrame> ITEM_FRAME;

  public static void registerEntities() {
    MINECART_WITH_CHEST = Registry.register(BuiltInRegistries.ENTITY_TYPE, LootrConstants.MINECART_WITH_CHEST, EntityType.Builder.of((EntityType.EntityFactory<LootrChestMinecartEntity>) LootrChestMinecartEntity::new, MobCategory.MISC).sized(0.9f, 1.4f).clientTrackingRange(8).build(LootrConstants.MINECART_WITH_CHEST_ENTITY));
    ITEM_FRAME = Registry.register(BuiltInRegistries.ENTITY_TYPE, LootrConstants.ITEM_FRAME, EntityType.Builder.<LootrItemFrame>of(LootrItemFrame::new, MobCategory.MISC)
        .sized(0.5F, 0.5F)
        .eyeHeight(0.0F)
        .clientTrackingRange(10)
        .updateInterval(Integer.MAX_VALUE).build(LootrConstants.ITEM_FRAME_ENTITY));
  }
}
