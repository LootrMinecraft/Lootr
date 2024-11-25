package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;

public class ModEntities {
  private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, LootrAPI.MODID);

  public static final DeferredHolder<EntityType<?>, EntityType<LootrChestMinecartEntity>> LOOTR_MINECART_ENTITY = REGISTER.register("lootr_minecart", () -> EntityType.Builder.of(LootrChestMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).build(ResourceKey.create(Registries.ENTITY_TYPE, LootrAPI.rl("lootr_minecart"))));

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
