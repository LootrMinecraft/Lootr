package noobanidus.mods.lootr.common.api.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;
import noobanidus.mods.lootr.common.mixins.AccessorMixinLootTable;
import org.jetbrains.annotations.NotNull;

public class DefaultBrushableLootFiller implements LootFiller {
  public static final DefaultBrushableLootFiller INSTANCE = new DefaultBrushableLootFiller();

  @Override
  public void unpackLootTable(@NotNull ILootrInfoProvider provider, @NotNull Player player, Container inventory) {
    Level level = provider.getInfoLevel();
    if (level == null || level.isClientSide() || level.getServer() == null) {
      LootrAPI.LOG.error("Unable to fill loot brushable as the level is null, client-side, or the server is null!");
      return;
    }
    BlockPos pos = provider.getInfoPos();
    ResourceKey<LootTable> lootTable = provider.getInfoLootTable();
    if (provider.isInfoReferenceInventory()) {
      LootrAPI.LOG.error("Unable to fill loot brushable in {} at {} as the provider is marked as a reference inventory, which is not supported for brushables!", level.dimension().location(), pos);
      return;
    }
    if (lootTable == null) {
      LootrAPI.LOG.error("Unable to fill loot container in {} at {} as the loot table is null!", level.dimension().location(), pos);
    } else {
      long seed = LootrAPI.getLootSeed(provider.getInfoLootSeed());
      LootTable loottable = level.getServer().reloadableRegistries().getLootTable(lootTable);

      if (loottable == LootTable.EMPTY) {
        LootrAPI.LOG.error("Unable to fill loot container in {} at {} as the loot table '{}' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.", level.dimension()
            .location(), pos, lootTable.location());
        if (LootrAPI.reportUnresolvedTables()) {
          player.displayClientMessage(LootrAPI.getInvalidTableComponent(lootTable), false);
        }
      }

      if (player instanceof ServerPlayer sPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger(sPlayer, lootTable);
      }

      LootParams params = new LootParams.Builder((ServerLevel) level)
          .withParameter(LootContextParams.ORIGIN, provider.getInfoVec())
          .withLuck(player.getLuck())
          .withParameter(LootContextParams.THIS_ENTITY, player)
          .create(LootContextParamSets.CHEST);

      LootContext context = new LootContext.Builder(params).withOptionalRandomSeed(seed).create(((AccessorMixinLootTable)loottable).getRandomSequence());

      ObjectArrayList<ItemStack> items = new ObjectArrayList<>();
      loottable.getRandomItems(context, items::add);

      LootFillerState state = new LootFillerState(provider, player, lootTable, loottable, inventory, params, seed);

      for (ILootrFilter filter : LootrAPI.getFilters()) {
        if (filter.mutate(items, state, context, context.getRandom())) {
          break;
        }
      }

      ItemStack theItem = switch (items.size()) {
        case 0 -> ItemStack.EMPTY;
        case 1 -> items.getFirst();
        default -> {
          LootrAPI.LOG.error("Brushable loot table '{}' in {} at {} returned multiple items, only one item is expected! Using the first item.", lootTable.location(), level.dimension().location(), pos);
          yield items.getFirst();
        }
      };

      inventory.setItem(0, theItem);
    }
  }
}
