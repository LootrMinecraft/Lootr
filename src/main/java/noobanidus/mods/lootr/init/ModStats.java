package noobanidus.mods.lootr.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.loot.conditions.LootCount;

public class ModStats {
  public static Stat<ResourceLocation> LOOTED_STAT;

  private static final DeferredRegister<ResourceLocation> REGISTER = DeferredRegister.create(BuiltInRegistries.CUSTOM_STAT, LootrAPI.MODID);

  public static final DeferredHolder<ResourceLocation, ResourceLocation> LOOTED_LOCATION = REGISTER.register("looted_stat", () -> new ResourceLocation(LootrAPI.MODID, "looted_stat"));

  public static void register (IEventBus bus) {
    REGISTER.register(bus);
  }

  public static void load() {
    LOOTED_STAT = Stats.CUSTOM.get(LOOTED_LOCATION.get());
  }
}
