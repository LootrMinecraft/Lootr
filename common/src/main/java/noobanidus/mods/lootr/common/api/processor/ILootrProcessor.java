package noobanidus.mods.lootr.common.api.processor;


import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Processors are applied to converted objects (entities or block entities)
 * during conversion. For block entities, pre-processors are applied after
 * a replacement block state (see ILootrBlockReplacementProvider) is determined
 * but before the level has been updated. For entities, pre-processors are
 * applied before the construction of the replacement entity.
 * <br />
 * For block entities, post-processors are applied after the level has been updated
 * and the loot table and loot table seed have been applied. For entities, post-
 * processors are applied after the replacement entity has been constructed
 * and the PlatformAPI.copyEntityData method has been called. Note: the replacement
 * entity is added via TickTask, so the post-processor may receive a level
 * that is not running on the main thread, and the entity will *not* be present
 * in the level.
 * <br />
 * See BlockEntityTicker::replaceEntitiesInChunk and HandleCart (NeoForge)
 * and MixinPersistentEntitySectionManager (Fabric) for the actual usage of these
 * processors.
 * <br />
 * These interfaces are not used directly. See ILootrBlockEntityProcessor.Pre/Post
 * and ILootrEntityProcessor.Pre/Post.
 * <br />
 * Processors are loaded via services. Specifically, the class implementing this
 * converter should be listed (fully qualified name) in a file located at:
 * META-INF/services/noobanidus.mods.lootr.common.api.processor.ILootrBlockEntityProcessor
 * or
 * META-INF/services/noobanidus.mods.lootr.common.api.processor.ILootrEntityProcessor
 */
sealed interface ILootrProcessor<T> permits ILootrProcessor.Post, ILootrProcessor.Pre {
  void process(ServerLevel level, @Nullable BlockPos position, T processee, @Nullable BlockState blockState, @NotNull ResourceKey<LootTable> lootTable, long lootTableSeed);

  sealed interface Post<T> extends ILootrProcessor<T> permits ILootrEntityProcessor.Post, ILootrBlockEntityProcessor.Post {
  }

  sealed interface Pre<T> extends ILootrProcessor<T> permits ILootrBlockEntityProcessor.Pre, ILootrEntityProcessor.Pre {
  }
}
