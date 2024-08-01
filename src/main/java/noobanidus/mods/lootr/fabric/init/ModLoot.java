package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.common.loot.conditions.LootCount;

public class ModLoot {
  public static final LootItemConditionType LOOT_COUNT = new LootItemConditionType(LootCount.CODEC);

  public static void registerLoot() {
    Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, LootrAPI.rl("loot_count"), LOOT_COUNT);
  }
}
