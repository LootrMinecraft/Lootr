package noobanidus.mods.lootr.common.impl.adapter;

import com.google.auto.service.AutoService;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrDataAdapter.class)
public class ItemFrameAdapter implements ILootrDataAdapter<ItemFrame> {
  // This is technically a no-op

  @Override
  public Class<ItemFrame> getAssignableClass() {
    return ItemFrame.class;
  }

  @Override
  public @Nullable ResourceKey<LootTable> getLootTable(ItemFrame entity) {
    return null;
  }

  @Override
  public long getLootSeed(ItemFrame entity) {
    return 0;
  }

  @Override
  public void setLootTable(ItemFrame entity, ResourceKey<LootTable> table, long seed) {

  }
}
