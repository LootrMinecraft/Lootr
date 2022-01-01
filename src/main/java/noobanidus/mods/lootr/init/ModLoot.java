package noobanidus.mods.lootr.init;

import net.minecraft.loot.LootConditionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.loot.condition.LootCount;

public class ModLoot {
  public static final LootConditionType LOOT_COUNT = new LootConditionType(new LootCount.Serializer());

  public static void register() {
    Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(Lootr.MODID, "loot_count"), LOOT_COUNT);
  }
}
