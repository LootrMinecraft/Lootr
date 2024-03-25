package noobanidus.mods.lootr.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.loot.conditions.LootCount;

public class ModLoot {

  private static final DeferredRegister<LootItemConditionType> REGISTER = DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, LootrAPI.MODID);

  public static final DeferredHolder<LootItemConditionType, LootItemConditionType> LOOT_COUNT = REGISTER.register("loot_count", () -> new LootItemConditionType(LootCount.CODEC));

  public static void register (IEventBus bus) {
    REGISTER.register(bus);
  }
}
