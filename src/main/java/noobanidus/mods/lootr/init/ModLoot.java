package noobanidus.mods.lootr.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.loot.conditions.LootCount;

public class ModLoot {
  public static final LootItemConditionType LOOT_COUNT = new LootItemConditionType(new LootCount.Serializer());

  public static void register() {
    Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(LootrAPI.MODID, "loot_count"), LOOT_COUNT);
  }
}
