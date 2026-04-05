package noobanidus.mods.lootr.common.api.filler;

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
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultLootFiller implements ILootFiller {
  private static final DefaultLootFiller INSTANCE = new DefaultLootFiller();
  private static LootFillerState state = null;

  public static DefaultLootFiller getInstance() {
    return INSTANCE;
  }

  @Nullable
  public static LootFillerState getFillerState() {
    return state;
  }

  @Override
  public void unpackLootTable(ILootrContainerInstance provider, @NotNull Player player, Container inventory) {
    Level level = provider.getDataLevel();
    if (level == null || level.isClientSide() || level.getServer() == null) {
      LootrAPI.LOG.error("Unable to fill loot container as the level is null, client-side, or the server is null!");
      return;
    }
    BlockPos pos = provider.getDataPos();
    ResourceKey<LootTable> lootTable = provider.getDataLootTable();
    if (provider.isDataReferenceInventory() && provider.getDataReferenceInventory() != null) {
      for (int i = 0; i < provider.getDataReferenceInventory().size(); i++) {
        inventory.setItem(i, provider.getDataReferenceInventory().get(i).copy());
      }
    } else if (lootTable == null) {
      LootrAPI.LOG.error("Unable to fill loot container in {} at {} as the loot table is null and the provider is not a reference inventory!", level.dimension()
          .identifier(), pos);
    } else {
      long seed = LootrAPI.getLootSeed(provider.getDataLootSeed());
      LootTable loottable = level.getServer().reloadableRegistries().getLootTable(lootTable);

      if (loottable == LootTable.EMPTY) {
        LootrAPI.LOG.error("Unable to fill loot container in {} at {} as the loot table '{}' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.", level.dimension()
            .identifier(), pos, lootTable.identifier());
        if (LootrAPI.reportUnresolvedTables()) {
          player.sendSystemMessage(LootrAPI.getInvalidTableComponent(lootTable));
        }
      }

      if (player instanceof ServerPlayer sPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger(sPlayer, lootTable);
      }

      LootParams.Builder builder = new LootParams.Builder((ServerLevel) level)
          .withParameter(LootContextParams.ORIGIN, provider.getDataVec())
          .withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);

      fill(provider, player, lootTable, loottable, inventory, builder.create(LootContextParamSets.CHEST), seed);
    }
  }

  // This is used to allow for storing the state during the loot filling process, allowing for our loot table mixin to apply
  public static void performFill(ILootrContainerInstance provider, Player player, ResourceKey<LootTable> lootTableKey, LootTable lootTable, Container container, LootParams parameters, long seed) {
    setFillerState(new LootFillerState(provider, player, lootTableKey, lootTable, container, parameters, seed));
    lootTable.fill(container, parameters, seed);
    setFillerState(null);
  }

  public static void setFillerState(@Nullable LootFillerState newState) {
    state = newState;
  }
}
