package noobanidus.mods.lootr.common.impl.adapter;

import com.google.auto.service.AutoService;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrDataAdapter.class)
public class MinecartChestAdapter implements ILootrDataAdapter<MinecartChest> {
  @Override
  public Class<MinecartChest> getAssignableClass() {
    return MinecartChest.class;
  }

  @Override
  public ResourceKey<LootTable> getLootTable(MinecartChest entity) {
    return entity.getLootTable();
  }

  @Override
  public long getLootSeed(MinecartChest entity) {
    return entity.getLootTableSeed();
  }

  @Override
  public void setLootTable(MinecartChest entity, ResourceKey<LootTable> table, long seed) {
    entity.setLootTable(table, seed);
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInventoryCopy(MinecartChest entity) {
    if (getLootTable(entity) == null) {
      NonNullList<ItemStack> result = NonNullList.withSize(entity.getContainerSize(), ItemStack.EMPTY);
      for (int i = 0; i < entity.getContainerSize(); i++) {
        result.set(i, entity.getItem(i).copy());
      }
      return result;
    }

    return null;
  }
}
