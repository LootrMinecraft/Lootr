package noobanidus.mods.lootr.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

public class ModEntities {
  public static EntityType<LootrChestMinecartEntity> LOOTR_MINECART_ENTITY;

  public static void registerEntities() {
    LOOTR_MINECART_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE, LootrAPI.rl("lootr_minecart"), EntityType.Builder.of(new EntityType.EntityFactory<LootrChestMinecartEntity>() {
      @Override
      public LootrChestMinecartEntity create(EntityType<LootrChestMinecartEntity> entityType, Level level) {
        return new LootrChestMinecartEntity(entityType, level);
      }
    }, MobCategory.MISC).sized(0.9f, 1.4f).clientTrackingRange(8).build());
  }
}
