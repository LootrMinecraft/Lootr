package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class LootrLootTableProvider {
  public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
    return new LootTableProvider(output, Set.of(LootrAPI.ELYTRA_CHEST), List.of(new LootTableProvider.SubProviderEntry(ChestLootTables::new, LootContextParamSets.CHEST)), provider);
  }

  public static class ChestLootTables implements LootTableSubProvider {
    public ChestLootTables(HolderLookup.Provider provider) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> p_249643_) {
      p_249643_.accept(
          LootrAPI.ELYTRA_CHEST,
          LootTable.lootTable()
              .withPool(
                  LootPool.lootPool()
                      .setRolls(ConstantValue.exactly(1))
                      .add(LootItem.lootTableItem(Items.ELYTRA).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))));
    }
  }
}
