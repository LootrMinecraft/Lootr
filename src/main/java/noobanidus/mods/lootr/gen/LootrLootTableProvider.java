package noobanidus.mods.lootr.gen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootrLootTableProvider extends LootTableProvider {
  private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = ImmutableList.of(Pair.of(ChestLootTables::new, LootContextParamSets.CHEST));

  public LootrLootTableProvider(DataGenerator p_124437_) {
    super(p_124437_);
  }

  @Override
  protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
    return tables;
  }

  @Override
  protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
    map.forEach((id, table) -> LootTables.validate(validationtracker, id, table));
  }

  public static class ChestLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> resourceLocationBuilderBiConsumer) {
      resourceLocationBuilderBiConsumer.accept(
          LootrAPI.ELYTRA_CHEST,
          LootTable.lootTable()
              .withPool(
                  LootPool.lootPool()
                      .setRolls(ConstantValue.exactly(1))
                      .add(LootItem.lootTableItem(Items.ELYTRA).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))));
    }
  }
}
