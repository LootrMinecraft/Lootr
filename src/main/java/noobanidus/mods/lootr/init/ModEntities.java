package noobanidus.mods.lootr.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class ModEntities {
  @SubscribeEvent
  public static void registerEntityType(RegistryEvent.Register<EntityEntry> event) {
    EntityRegistry.registerModEntity(new ResourceLocation(Lootr.MODID, "lootr_minecart"), LootrChestMinecartEntity.class, "lootr_minecart",1, Lootr.instance,64, 1, false);
  }
}
