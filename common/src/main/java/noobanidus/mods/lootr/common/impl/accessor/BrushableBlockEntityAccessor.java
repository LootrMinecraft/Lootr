package noobanidus.mods.lootr.common.impl.accessor;

import com.google.auto.service.AutoService;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrDataAccessor;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinBrushableBlockEntity;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrDataAccessor.class)
public class BrushableBlockEntityAccessor implements ILootrDataAccessor<BrushableBlockEntity> {
  @Override
  public Class<BrushableBlockEntity> getAssignableClass() {
    return BrushableBlockEntity.class;
  }

  @Override
  public ResourceKey<LootTable> getLootTable(BrushableBlockEntity entity) {
    return ((AccessorMixinBrushableBlockEntity) entity).lootr$getLootTable();
  }

  @Override
  public long getLootSeed(BrushableBlockEntity entity) {
    return ((AccessorMixinBrushableBlockEntity) entity).lootr$getLootTableSeed();
  }

  @Override
  public void setLootTable(BrushableBlockEntity entity, ResourceKey<LootTable> table, long seed) {
    entity.setLootTable(table, seed);
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInventoryCopy(BrushableBlockEntity entity) {
    if (getLootTable(entity) == null) {
      NonNullList<ItemStack> result = NonNullList.withSize(1, ItemStack.EMPTY);
      result.set(0, entity.getItem());
      return result;
    }

    return null;
  }
}
