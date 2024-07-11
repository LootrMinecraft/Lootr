package noobanidus.mods.lootr.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;


public class ClientHandlers {
  public static void handleCloseCart(int entityId) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as closed as level is null.");
      return;
    }
    Entity cart = level.getEntity(entityId);
    if (cart == null) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as closed as entity is null.");
      return;
    }

    if (!(cart instanceof ILootrCart lootrCart)) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as closed as entity is not a Lootr minecart.");
      return;
    }

    lootrCart.setClientOpened(false);
  }

  public static void handleOpenCart(int entityId) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as opened as level is null.");
      return;
    }
    Entity cart = level.getEntity(entityId);
    if (cart == null) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as opened as entity is null.");
      return;
    }

    if (!(cart instanceof ILootrCart lootrCart)) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as opened as entity is not a Lootr minecart.");
      return;
    }

    lootrCart.setClientOpened(true);
  }

  public static void handleOpenContainer(BlockPos pos) {
    if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof ILootrBlockEntity provider) {
      provider.setClientOpened(true);

      SectionPos sPos = SectionPos.of(pos);
      Minecraft.getInstance().levelRenderer.setSectionDirty(sPos.x(), sPos.y(), sPos.z());
    }
  }

  public static void handleCloseContainer(BlockPos pos) {
    if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof ILootrBlockEntity provider) {
      provider.setClientOpened(false);

      SectionPos sPos = SectionPos.of(pos);
      Minecraft.getInstance().levelRenderer.setSectionDirty(sPos.x(), sPos.y(), sPos.z());
    }
  }
}
