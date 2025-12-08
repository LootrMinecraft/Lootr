package noobanidus.mods.lootr.common.impl.adapter;

import com.google.auto.service.AutoService;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;

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
}
