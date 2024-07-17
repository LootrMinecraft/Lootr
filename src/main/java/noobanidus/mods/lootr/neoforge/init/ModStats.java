package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.api.LootrAPI;

public class ModStats {
  private static final DeferredRegister<ResourceLocation> REGISTER = DeferredRegister.create(BuiltInRegistries.CUSTOM_STAT, LootrAPI.MODID);
  public static final DeferredHolder<ResourceLocation, ResourceLocation> LOOTED_LOCATION = REGISTER.register("looted_stat", () -> LootrAPI.rl("looted_stat"));
  public static Stat<ResourceLocation> LOOTED_STAT;

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }

  public static void load() {
    LOOTED_STAT = Stats.CUSTOM.get(LOOTED_LOCATION.get());
  }
}
