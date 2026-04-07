package noobanidus.mods.lootr.common.impl.accessor;

import com.google.auto.service.AutoService;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrDataAccessor;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrDataAccessor.class)
public class RandomizableContainerBlockEntityAccessor implements ILootrDataAccessor<RandomizableContainerBlockEntity> {

  @Override
  public Class<RandomizableContainerBlockEntity> getAssignableClass() {
    return RandomizableContainerBlockEntity.class;
  }

  @Override
  public ResourceKey<LootTable> getLootTable(RandomizableContainerBlockEntity entity) {
    return entity.getLootTable();
  }

  @Override
  public long getLootSeed(RandomizableContainerBlockEntity entity) {
    return entity.getLootTableSeed();
  }

  @Override
  public void setLootTable(RandomizableContainerBlockEntity entity, ResourceKey<LootTable> table, long seed) {
    entity.setLootTable(table, seed);
  }

  @Override
  public int priority() {
    return -100;
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInventoryCopy(RandomizableContainerBlockEntity entity) {
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
