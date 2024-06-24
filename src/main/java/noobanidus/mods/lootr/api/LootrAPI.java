package noobanidus.mods.lootr.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class LootrAPI {
  public static final Logger LOG = LogManager.getLogger();
  public static final String MODID = "lootr";
  public static final String NETWORK_VERSION = "lootr-1.21.0-1";
  public static final ResourceKey<LootTable> ELYTRA_CHEST = ResourceKey.create(Registries.LOOT_TABLE, LootrAPI.rl("chests/elytra"));

  public static ILootrAPI INSTANCE;

  public static ResourceLocation rl (String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }

  public static ResourceLocation rl (String namespace, String path) {
    return ResourceLocation.fromNamespaceAndPath(namespace, path);
  }

  public static boolean shouldDiscardIdAndOpeners;

  public static boolean isFakePlayer (Player player) {
    return INSTANCE.isFakePlayer(player);
  }

  public static boolean clearPlayerLoot(ServerPlayer entity) {
    return INSTANCE.clearPlayerLoot(entity);
  }

  public static boolean clearPlayerLoot(UUID id) {
    return INSTANCE.clearPlayerLoot(id);
  }

  public static long getLootSeed(long seed) {
    return INSTANCE.getLootSeed(seed);
  }

  public static boolean shouldDiscard() {
    return INSTANCE.shouldDiscard();
  }

  public static float getExplosionResistance(Block block, float defaultResistance) {
    return INSTANCE.getExplosionResistance(block, defaultResistance);
  }

  public static float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress) {
    return INSTANCE.getDestroyProgress(state, player, level, position, defaultProgress);
  }

  public static int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal) {
    return INSTANCE.getAnalogOutputSignal(pBlockState, pLevel, pPos, defaultSignal);
  }

  // TODO: Consider if this is really needed
  public static boolean hasCapacity(String capacity) {
    return INSTANCE.hasCapacity(capacity);
  }
}
