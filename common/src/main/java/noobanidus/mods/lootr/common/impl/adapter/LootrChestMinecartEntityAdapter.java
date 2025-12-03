package noobanidus.mods.lootr.common.impl.adapter;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;

public class LootrChestMinecartEntityAdapter implements ILootrDataAdapter<LootrChestMinecartEntity> {
  @Override
  public Class<LootrChestMinecartEntity> getAssignableClass() {
    return LootrChestMinecartEntity.class;
  }

  @Override
  public ResourceKey<LootTable> getLootTable(LootrChestMinecartEntity entity) {
    return entity.getInfoLootTable();
  }

  @Override
  public long getLootSeed(LootrChestMinecartEntity entity) {
    return entity.getLootTableSeed();
  }

  @Override
  public void setLootTable(LootrChestMinecartEntity entity, ResourceKey<LootTable> table, long seed) {
    entity.setLootTable(table, seed);
  }
}
