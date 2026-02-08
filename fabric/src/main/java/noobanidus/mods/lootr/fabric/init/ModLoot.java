package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.loot.conditions.LootCount;

public class ModLoot {
  public static void registerLoot() {
    Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, LootrAPI.rl("loot_count"), LootCount.CODEC);
  }
}
