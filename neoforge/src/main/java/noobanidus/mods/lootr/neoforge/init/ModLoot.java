package noobanidus.mods.lootr.neoforge.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.loot.conditions.LootCount;

public class ModLoot {

  private static final DeferredRegister<MapCodec<? extends LootItemCondition>> REGISTER = DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, LootrAPI.MODID);

  public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<? extends LootItemCondition>> LOOT_COUNT = REGISTER.register("loot_count", () -> LootCount.CODEC);

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
