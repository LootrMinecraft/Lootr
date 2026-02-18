package noobanidus.mods.lootr.common.impl.adapter;

import com.google.auto.service.AutoService;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinBrushableBlockEntity;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrDataAdapter.class)
public class BrushableBlockEntityAdapter implements ILootrDataAdapter<BrushableBlockEntity> {
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
  public @Nullable NonNullList<ItemStack> getInventory(BrushableBlockEntity entity) {
    return null;
  }
}
