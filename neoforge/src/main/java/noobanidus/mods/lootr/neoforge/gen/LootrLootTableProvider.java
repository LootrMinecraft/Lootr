package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.neoforge.init.ModBlocks;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class LootrLootTableProvider {
  public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
    return new LootTableProvider(output, Set.of(LootrAPI.ELYTRA_CHEST), List.of(new LootTableProvider.SubProviderEntry(ChestLootTables::new, LootContextParamSets.CHEST), new LootTableProvider.SubProviderEntry(LootrBlockLootTables::new, LootContextParamSets.BLOCK)), provider);
  }

  public static class LootrBlockLootTables extends BlockLootSubProvider {
    protected LootrBlockLootTables(HolderLookup.Provider arg2) {
      super(Set.of(), FeatureFlags.REGISTRY.allFlags(), arg2);
    }

    protected LootTable.Builder lootrBlockDrop(Block block) {
      return LootTable.lootTable()
          .withPool(this.applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f))
              .add(LootItem.lootTableItem(block)
                  .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                      .include(DataComponents.CUSTOM_NAME)))));
    }

    @Override
    protected void generate() {
      this.add(ModBlocks.CHEST.get(), lootrBlockDrop(Blocks.CHEST));
      this.add(ModBlocks.BARREL.get(), lootrBlockDrop(Blocks.BARREL));
      this.add(ModBlocks.INVENTORY.get(), lootrBlockDrop(Blocks.CHEST));
      this.add(ModBlocks.TRAPPED_CHEST.get(), lootrBlockDrop(Blocks.TRAPPED_CHEST));
      this.add(ModBlocks.SHULKER_BOX.get(), lootrBlockDrop(Blocks.SHULKER_BOX));
      this.dropSelf(ModBlocks.TROPHY.get());
    }

    @Override
    protected @NonNull Iterable<Block> getKnownBlocks() {
      return List.of(ModBlocks.CHEST.get(), ModBlocks.BARREL.get(), ModBlocks.INVENTORY.get(), ModBlocks.TRAPPED_CHEST.get(), ModBlocks.SHULKER_BOX.get(), ModBlocks.TROPHY.get());
    }
  }

  public static class ChestLootTables implements LootTableSubProvider {
    public ChestLootTables(HolderLookup.Provider provider) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
      consumer.accept(
          LootrAPI.ELYTRA_CHEST,
          LootTable.lootTable()
              .withPool(
                  LootPool.lootPool()
                      .setRolls(ConstantValue.exactly(1))
                      .add(LootItem.lootTableItem(Items.ELYTRA)
                          .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))));
      consumer.accept(
          LootrAPI.TROPHY_REWARD,
          LootTable.lootTable()
              .withPool(
                  LootPool.lootPool()
                      .setRolls(ConstantValue.exactly(1))
                      .add(LootItem.lootTableItem(LootrRegistry.getTrophyBlock())
                          .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))));
      consumer.accept(
          LootrAPI.ITEM_FRAME_EMPTY,
          LootTable.lootTable()
      );
    }
  }
}
