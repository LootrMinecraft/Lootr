package noobanidus.mods.lootr.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

public class ModEntities {
  private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LootrAPI.MODID);

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }

  public static final RegistryObject<EntityType<LootrChestMinecartEntity>> LOOTR_MINECART_ENTITY = REGISTER.register("lootr_minecart", () -> EntityType.Builder.<LootrChestMinecartEntity>of(LootrChestMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).setCustomClientFactory((entity, world) -> new LootrChestMinecartEntity(ModEntities.LOOTR_MINECART_ENTITY.get(), world)).build("lootr_minecart"));


}
