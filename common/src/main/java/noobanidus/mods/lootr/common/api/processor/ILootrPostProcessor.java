package noobanidus.mods.lootr.common.api.processor;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ILootrPostProcessor {
  void process (ServerLevel level, BlockPos position, RandomizableContainerBlockEntity newBlockEntity, BlockState newState, ResourceKey<LootTable> lootTable);
}
