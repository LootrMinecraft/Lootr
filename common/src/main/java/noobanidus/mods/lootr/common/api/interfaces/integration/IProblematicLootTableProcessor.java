package noobanidus.mods.lootr.common.api.interfaces.integration;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Set;

public interface IProblematicLootTableProcessor {
  Set<ResourceKey<LootTable>> gatherProblematicChests();

  default Set<ResourceKey<LootTable>> processProblematicChests(Set<ResourceKey<LootTable>> problematicChests) {
    return problematicChests;
  }

  default int priority() {
    return 0;
  }
}
