package noobanidus.mods.lootr.common.api.processor;


import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ILootrProcessor {
  void process(ServerLevel level, BlockPos position, BlockEntity blockEntity, BlockState blockState, ResourceKey<LootTable> lootTable, long lootTableSeed);
}
