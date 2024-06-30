package noobanidus.mods.lootr.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.api.client.ClientTextureType;
import noobanidus.mods.lootr.api.inventory.ILootrInventory;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface ILootrAPI {
  MinecraftServer getServer ();

  boolean isFakePlayer (Player player);

  default boolean clearPlayerLoot(ServerPlayer entity) {
    return clearPlayerLoot(entity.getUUID());
  }

  boolean clearPlayerLoot(UUID id);

  /**
   * Provides access to a Lootr-instanced container/MenuProvider for the relevant non-Lootr and non-Vanilla container.
   * <p>
   * This should be called via an integration class in the relevant `use` method of a block.
   * <p>
   * Requirements include a UUID generated for the specific container. Some implementation of `LootFiller` should exist in the relevant block entity. Likewise, you will need to provide functional references in order to fill in the `lootTable` and `lootTableSeed`.
   * <p>
   * If your class does not derive from `BaseContainerBlockEntity`, please use the alternate method that accepts an `IntSupplier` (for the size of the container) and a `Supplier<Component>` for the name of the block entity.
   *
   * @param level         The relevant level.
   * @param id            The universally unique identifier for this block entity.
   * @param pos           The block position where this block entity is located.
   * @param player        The ServerPlayer currently accessing the container.
   * @param blockEntity   The instance of your block entity, extending `BaseContainerBlockEntity`
   * @param filler        A functional interface that accepts the container to be filled, the loot table, the seed, and the player who is opening the container.
   * @param tableSupplier A method reference to the loot table of this block entity.
   * @param seedSupplier  A method reference to the loot table seed of this block entity.
   * @return Either the relevant inventory (cast as a MenuProvider) or null if the function was called with a client-size Level or an instance of Level that isn't ServerLevel.
   */
  @Nullable
  ILootrInventory getInventory(Level level, UUID id, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceKey<LootTable>> tableSupplier, LongSupplier seedSupplier);

  @Nullable
  ILootrInventory getInventory(Level level, UUID id, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceKey<LootTable>> tableSupplier, LongSupplier seedSupplier, MenuBuilder builder);

  /**
   * Provides access to an instanced player container for the relevant block entity. Instead of requiring the block entity extend BaseContainerBlockEntity, this instead accepts an IntSupplier (the size of the container) and a `Supplier<Component>` equivalent to `BaseContainerBlockEntity::getDisplayName`.
   * <p>
   * If your block entity extends BaseContainerBlockEntity, please use the method that accepts that instead.
   * <p>
   * See the documentation of the other `getModdedMenu` for more details.
   */
  @Nullable
  ILootrInventory getInventory(Level level, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceKey<LootTable>> tableSupplier, LongSupplier seedSupplier);

  @Nullable
  ILootrInventory getInventory(Level level, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceKey<LootTable>> tableSupplier, LongSupplier seedSupplier, MenuBuilder builder);

  /**
   * Provides access to the relevant configuration for the loot seed. This is used to determine if the provided seed is randomized or not.
   */
  long getLootSeed(long seed);

  boolean shouldDiscard();

  float getExplosionResistance(Block block, float defaultResistance);

  float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress);

  int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal);

  boolean shouldNotify (int remaining);

  ClientTextureType getTextureType();

  default boolean isOldTextures () {
    return getTextureType() == ClientTextureType.OLD;
  }

  default boolean isVanillaTextures () {
    return getTextureType() == ClientTextureType.VANILLA;
  }

  default boolean isDefaultTextures () {
    return getTextureType() == ClientTextureType.DEFAULT;
  }

  boolean isDisabled();

  boolean isLootTableBlacklisted (ResourceKey<LootTable> table);

  boolean isDimensionBlocked (ResourceKey<Level> dimension);

  boolean isDimensionDecaying (ResourceKey<Level> dimension);

  boolean isDimensionRefreshing (ResourceKey<Level> dimension);

  boolean isDecaying (ILootInfoProvider provider);

  boolean isRefreshing (ILootInfoProvider provider);

  boolean reportUnresolvedTables ();

  boolean isCustomTrapped ();

  boolean isWorldBorderSafe(Level level, BlockPos pos);

  boolean isWorldBorderSafe(Level level, ChunkPos pos);

  boolean hasExpired (long time);

  boolean shouldConvertMineshafts ();

  boolean shouldConvertElytras ();

  int getDecayValue ();

  int getRefreshValue ();

  Style getInvalidStyle();

  Style getDecayStyle();

  Style getRefreshStyle();

  Style getChatStyle ();

  Component getInvalidTableComponent (ResourceKey<LootTable> lootTable) ;

  boolean canDestroyOrBreak (Player player);

  boolean isBreakDisabled ();

  @Nullable
  BlockState replacementBlockState (BlockState original);

  // TODO: Think on this.
  default boolean hasCapacity(String capacity) {
    return switch (capacity) {
      case LootrCapacities.STRUCTURE_SAVING -> true;
      case LootrCapacities.SHOULD_DISCARD -> true;
      case LootrCapacities.CAPACITIES -> true;
      case LootrCapacities.EXPLOSION_RESISTANCE -> true;
      case LootrCapacities.DESTRUCTION_PROGRESS -> true;
      default -> false;
    };
  }
}
