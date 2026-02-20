package noobanidus.mods.lootr.common.impl.adapter;

import com.google.auto.service.AutoService;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrDataAdapter.class)
public class DecoratedPotBlockEntityAdapter implements ILootrDataAdapter<DecoratedPotBlockEntity> {
  @Override
  public Class<DecoratedPotBlockEntity> getAssignableClass() {
    return DecoratedPotBlockEntity.class;
  }

  @Override
  public @Nullable ResourceKey<LootTable> getLootTable(DecoratedPotBlockEntity entity) {
    return entity.getLootTable();
  }

  @Override
  public long getLootSeed(DecoratedPotBlockEntity entity) {
    return entity.getLootTableSeed();
  }

  @Override
  public void setLootTable(DecoratedPotBlockEntity entity, ResourceKey<LootTable> table, long seed) {
    entity.setLootTable(table, seed);
  }

  @Override
  public boolean hasCopyableComponentsViaItem(DecoratedPotBlockEntity entity) {
    return LootrAPI.getDecorationsAdapter(entity) != PotDecorationsAdapter.EMPTY;
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInventoryCopy(DecoratedPotBlockEntity entity) {
    if (getLootTable(entity) == null) {
      NonNullList<ItemStack> result = NonNullList.withSize(1, ItemStack.EMPTY);
      result.set(0, entity.getTheItem());
      return result;
    }

    return null;
  }
}
