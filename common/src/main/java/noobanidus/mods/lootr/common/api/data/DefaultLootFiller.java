package noobanidus.mods.lootr.common.api.data;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

public class DefaultLootFiller implements LootFiller {
  private static final DefaultLootFiller INSTANCE = new DefaultLootFiller();
  private static LootFillerState state = null;

  public static DefaultLootFiller getInstance() {
    return INSTANCE;
  }

  @Nullable
  public static LootFillerState getFillerState () {
    return state;
  }

  @Override
  public void unpackLootTable(ILootrInfoProvider provider, Player player, Container inventory) {
    Level level = provider.getInfoLevel();
    BlockPos pos = provider.getInfoPos();
    ResourceKey<LootTable> lootTable = provider.getInfoLootTable();
    if (provider.isInfoReferenceInventory()) {
        for (int i = 0; i < provider.getInfoReferenceInventory().size(); i++) {
          inventory.setItem(i, provider.getInfoReferenceInventory().get(i).copy());
        }
    } else if (lootTable == null) {
      LootrAPI.LOG.error("Unable to fill loot container in " + level.dimension().location() + " at " + pos + " as the loot table is null and the provider is not a reference inventory!");
      // Unknown TODO: what exactly was supposed to be done here?
    } else {
      long seed = LootrAPI.getLootSeed(provider.getInfoLootSeed());
      LootTable loottable = level.getServer().reloadableRegistries().getLootTable(lootTable);

      if (loottable == LootTable.EMPTY) {
        LootrAPI.LOG.error("Unable to fill loot container in " + level.dimension().location() + " at " + pos + " as the loot table '" + lootTable.location() + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
        if (LootrAPI.reportUnresolvedTables()) {
          player.displayClientMessage(LootrAPI.getInvalidTableComponent(lootTable), false);
        }
      }

      if (player instanceof ServerPlayer sPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger(sPlayer, lootTable);
      }

      LootParams.Builder builder = new LootParams.Builder((ServerLevel) level)
          .withParameter(LootContextParams.ORIGIN, provider.getInfoVec());
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
      }

      LootFiller.super.fill(provider, player, lootTable, loottable, inventory, builder.create(LootContextParamSets.CHEST), seed);
    }
  }

  public static void performFill (ILootrInfoProvider provider, Player player, ResourceKey<LootTable> lootTableKey, LootTable lootTable, Container container, LootParams parameters, long seed) {
    state = new LootFillerState(provider, player, lootTableKey, lootTable, container, parameters, seed);
    lootTable.fill(container, parameters, seed);
    state = null;
  }
}
