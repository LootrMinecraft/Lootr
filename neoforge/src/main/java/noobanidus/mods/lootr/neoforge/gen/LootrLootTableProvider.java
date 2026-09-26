package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
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
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.neoforge.init.ModBlocks;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;

public class LootrLootTableProvider {
  public static SingleRegistryBootstrap<LootTable> create() {
    return new LootTableProvider(Set.of(LootrAPI.ELYTRA_CHEST, LootrAPI.ITEM_FRAME_EMPTY, LootrAPI.TROPHY_REWARD), List.of(new LootTableProvider.SubProviderEntry(ChestLootTables::new, LootContextParamSets.CHEST), new LootTableProvider.SubProviderEntry(LootrBlockLootTables::new, LootContextParamSets.BLOCK)));
  }

  public static class LootrBlockLootTables extends BlockLootSubProvider {
    protected LootrBlockLootTables(Context context) {
      super(Set.of(), FeatureFlags.REGISTRY.allFlags(), context);
    }

    protected LootTable.Builder lootrBlockDrop(Block block) {
      return LootTable.lootTable()
          .withPool(this.applyExplosionCondition(block, LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
              .add(LootItem.lootTableItem(block)
                  .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                      .include(DataComponents.CUSTOM_NAME)))));
    }

    @Override
    protected void generate() {
      this.add(ModBlocks.CHEST.get(), lootrBlockDrop(Blocks.CHEST));
      this.add(ModBlocks.BARREL.get(), lootrBlockDrop(Blocks.BARREL));
      this.add(ModBlocks.COPPER_CHEST.get(), lootrBlockDrop(Blocks.COPPER_CHEST.waxed().unaffected()));
      this.add(ModBlocks.WEATHERED_COPPER_CHEST.get(), lootrBlockDrop(Blocks.COPPER_CHEST.waxed().weathered()));
      this.add(ModBlocks.OXIDIZED_COPPER_CHEST.get(), lootrBlockDrop(Blocks.COPPER_CHEST.waxed().oxidized()));
      this.add(ModBlocks.EXPOSED_COPPER_CHEST.get(), lootrBlockDrop(Blocks.COPPER_CHEST.waxed().exposed()));
      this.add(ModBlocks.TRAPPED_CHEST.get(), lootrBlockDrop(Blocks.TRAPPED_CHEST));
      this.add(ModBlocks.SHULKER_BOX.get(), lootrBlockDrop(Blocks.SHULKER_BOX));
      this.add(ModBlocks.DECORATED_POT.get(), LootTable.lootTable());
      this.add(ModBlocks.SUSPICIOUS_SAND.get(), lootrBlockDrop(Blocks.SAND));
      this.add(ModBlocks.SUSPICIOUS_GRAVEL.get(), lootrBlockDrop(Blocks.GRAVEL));
      this.dropSelf(ModBlocks.TROPHY.get());
    }

    @Override
    protected @NonNull Iterable<Block> getKnownBlocks() {
      return List.of(ModBlocks.CHEST.get(), ModBlocks.BARREL.get(), ModBlocks.TRAPPED_CHEST.get(), ModBlocks.SHULKER_BOX.get(), ModBlocks.TROPHY.get(), ModBlocks.SUSPICIOUS_GRAVEL.get(), ModBlocks.SUSPICIOUS_SAND.get(), ModBlocks.DECORATED_POT.get(), ModBlocks.EXPOSED_COPPER_CHEST.get(), ModBlocks.COPPER_CHEST.get(), ModBlocks.WEATHERED_COPPER_CHEST.get(), ModBlocks.OXIDIZED_COPPER_CHEST.get());
    }
  }

  public static class ChestLootTables implements LootTableSubProvider {
    private final Context consumer;

    public ChestLootTables(Context context) {
      this.consumer = context;
    }

    @Override
    public void run() {
      consumer.accept(
          LootrAPI.ELYTRA_CHEST,
          LootTable.lootTable()
              .withPool(
                  LootPool.lootPool()
                      .setRolls(ContextIntProviders.exactly(1))
                      .add(LootItem.lootTableItem(Items.ELYTRA)
                          .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(1))))));
      consumer.accept(
          LootrAPI.TROPHY_REWARD,
          LootTable.lootTable()
              .withPool(
                  LootPool.lootPool()
                      .setRolls(ContextIntProviders.exactly(10))
                      .add(LootItem.lootTableItem(LootrRegistry.getTrophyBlock())
                          .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(1))))));
      consumer.accept(
          LootrAPI.ITEM_FRAME_EMPTY,
          LootTable.lootTable()
      );
    }
  }
}
