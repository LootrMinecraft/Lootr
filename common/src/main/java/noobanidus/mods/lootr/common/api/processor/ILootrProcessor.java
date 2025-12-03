package noobanidus.mods.lootr.common.api.processor;


import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

sealed interface ILootrProcessor<T> permits ILootrProcessor.Post, ILootrProcessor.Pre {
  void process(ServerLevel level, @Nullable BlockPos position, T processee, @Nullable BlockState blockState, @NotNull ResourceKey<LootTable> lootTable, long lootTableSeed);

  sealed interface Post<T> extends ILootrProcessor<T> permits ILootrEntityProcessor.Post, ILootrBlockEntityProcessor.Post {
  }

  sealed interface Pre<T> extends ILootrProcessor<T> permits ILootrBlockEntityProcessor.Pre, ILootrEntityProcessor.Pre {
  }
}
