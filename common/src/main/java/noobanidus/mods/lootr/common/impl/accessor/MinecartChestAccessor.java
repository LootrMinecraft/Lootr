package noobanidus.mods.lootr.common.impl.accessor;

import com.google.auto.service.AutoService;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrDataAccessor;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrDataAccessor.class)
public class MinecartChestAccessor implements ILootrDataAccessor<MinecartChest> {
  @Override
  public Class<MinecartChest> getAssignableClass() {
    return MinecartChest.class;
  }

  @Override
  public ResourceKey<LootTable> getLootTable(MinecartChest entity) {
    return entity.getContainerLootTable();
  }

  @Override
  public long getLootSeed(MinecartChest entity) {
    return entity.getContainerLootTableSeed();
  }

  @Override
  public void setLootTable(MinecartChest entity, ResourceKey<LootTable> table, long seed) {
    entity.setLootTable(table, seed);
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInventoryCopy(MinecartChest entity) {
    if (getLootTable(entity) == null) {
      NonNullList<ItemStack> copy = NonNullList.withSize(entity.getContainerSize(), ItemStack.EMPTY);
      for (int i = 0; i < copy.size(); i++) {
        copy.set(i, entity.getItem(i).copy());
      }
      return copy;
    }

    return null;
  }
}
