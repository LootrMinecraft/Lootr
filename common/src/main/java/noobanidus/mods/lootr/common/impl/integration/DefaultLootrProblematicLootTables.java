package noobanidus.mods.lootr.common.impl.integration;

import com.google.auto.service.AutoService;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.integration.IProblematicLootTableProcessor;

import java.util.Set;

@AutoService(IProblematicLootTableProcessor.class)
public class DefaultLootrProblematicLootTables implements IProblematicLootTableProcessor {
  @Override
  public Set<ResourceKey<LootTable>> gatherProblematicChests() {
    return LootrAPI.PROBLEMATIC_LOOT_TABLES;
  }

  @Override
  public int priority() {
    return -1000;
  }
}
