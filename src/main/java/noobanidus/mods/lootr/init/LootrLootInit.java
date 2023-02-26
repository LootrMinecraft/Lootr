package noobanidus.mods.lootr.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.LootrModConfig;
import noobanidus.mods.lootr.loot.conditions.LootCount;

public class LootrLootInit {
  public static final LootItemConditionType LOOT_COUNT = new LootItemConditionType(new LootCount.Serializer());

  public static void registerLoot() {
    Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(LootrAPI.MODID, "loot_count"), LOOT_COUNT);

    if (LootrModConfig.get().debug.debugMode) {
      LootrAPI.LOG.info("Lootr: Common Registry - Loot Registered");
    }
  }
}
