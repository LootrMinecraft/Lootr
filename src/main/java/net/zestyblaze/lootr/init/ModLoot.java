package net.zestyblaze.lootr.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.loot.conditions.LootCount;

public class ModLoot {
  public static final LootItemConditionType LOOT_COUNT = new LootItemConditionType(new LootCount.Serializer());

  public static void registerLoot() {
    Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation(LootrAPI.MODID, "loot_count"), LOOT_COUNT);
  }
}
