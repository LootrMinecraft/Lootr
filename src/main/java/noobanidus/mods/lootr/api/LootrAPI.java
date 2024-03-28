package noobanidus.mods.lootr.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class LootrAPI {
  public static final Logger LOG = LogManager.getLogger();
  public static final String MODID = "lootr";
  public static final ResourceLocation ELYTRA_CHEST = new ResourceLocation(MODID, "chests/elytra");

  public static ILootrAPI INSTANCE;

  public static boolean shouldDiscardIdAndOpeners;

  public static boolean clearPlayerLoot(ServerPlayer entity) {
    return INSTANCE.clearPlayerLoot(entity);
  }

  public static boolean clearPlayerLoot(UUID id) {
    return INSTANCE.clearPlayerLoot(id);
  }

  public static MenuProvider getModdedMenu(Level level, UUID id, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
    return INSTANCE.getModdedMenu(level, id, pos, player, blockEntity, filler, tableSupplier, seedSupplier);
  }

  public static MenuProvider getModdedMenu(Level level, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
    return INSTANCE.getModdedMenu(level, id, pos, player, sizeSupplier, displaySupplier, filler, tableSupplier, seedSupplier);
  }

  public static boolean isSavingStructure () {
    return shouldDiscard();
  }

  public static boolean shouldDiscard() {
    return INSTANCE.isSavingStructure();
  }
}
