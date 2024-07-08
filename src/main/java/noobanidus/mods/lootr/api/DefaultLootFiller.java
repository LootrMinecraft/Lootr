package noobanidus.mods.lootr.api;

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
import noobanidus.mods.lootr.api.info.ILootrInfoProvider;

public class DefaultLootFiller implements LootFiller {
  private static DefaultLootFiller INSTANCE = new DefaultLootFiller();

  public static DefaultLootFiller getInstance() {
    return INSTANCE;
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
      // TODO:
    } else {
      long seed = provider.getInfoLootSeed();
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

      loottable.fill(inventory, builder.create(LootContextParamSets.CHEST), LootrAPI.getLootSeed(seed));
    }
  }
}
