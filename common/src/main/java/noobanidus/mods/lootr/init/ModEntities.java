package noobanidus.mods.lootr.init;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

public class ModEntities {
  public static EntityType<LootrChestMinecartEntity> LOOTR_MINECART_ENTITY = EntityType.Builder.<LootrChestMinecartEntity>of(LootrChestMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).setCustomClientFactory((entity, world) -> new LootrChestMinecartEntity(ModEntities.LOOTR_MINECART_ENTITY, world)).build("lootr_minecart");

  static {
    LOOTR_MINECART_ENTITY.setRegistryName(Lootr.MODID, "lootr_minecart");
  }

  public static void registerEntityType(RegistryEvent.Register<EntityType<?>> event) {
    event.getRegistry().register(LOOTR_MINECART_ENTITY);
  }
}
