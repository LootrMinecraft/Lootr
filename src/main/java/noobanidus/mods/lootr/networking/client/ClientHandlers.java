package noobanidus.mods.lootr.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.network.NetworkEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.networking.CloseCart;
import noobanidus.mods.lootr.networking.OpenCart;
import noobanidus.mods.lootr.networking.UpdateModelData;

import java.util.function.Supplier;

public class ClientHandlers {
  public static void handleUpdateModel(UpdateModelData message, Supplier<NetworkEvent.Context> context) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      Lootr.LOG.info("Unable to update model data for location '" + message.pos + "' as level is null.");
      return;
    }

    Player player = Minecraft.getInstance().player;
    if (player == null) {
      Lootr.LOG.info("Unable to update model data for location '" + message.pos + "' as player is null.");
      return;
    }

    BlockEntity be = level.getBlockEntity(message.pos);
    if (be instanceof ILootTile tile) {
      tile.getOpeners().remove(player.getUUID());
      ModelDataManager.requestModelDataRefresh(be);
    } else {
      Lootr.LOG.info("Unable to update model data for location '" + message.pos + "' as block entity is null or not a Lootr block entity.");
    }
  }

  public static void handleOpenCart(OpenCart message, Supplier<NetworkEvent.Context> context) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as level is null.");
      return;
    }
    Entity cart = level.getEntity(message.entityId);
    if (cart == null) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as entity is null.");
      return;
    }

    if (!(cart instanceof LootrChestMinecartEntity)) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as entity is not a Lootr minecart.");
      return;
    }

    ((LootrChestMinecartEntity) cart).setOpened();
  }

  public static void handleCloseCart(CloseCart message, Supplier<NetworkEvent.Context> context) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as closed as level is null.");
      return;
    }
    Entity cart = level.getEntity(message.entityId);
    if (cart == null) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as closed as entity is null.");
      return;
    }

    if (!(cart instanceof LootrChestMinecartEntity)) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as closed as entity is not a Lootr minecart.");
      return;
    }

    ((LootrChestMinecartEntity) cart).setClosed();
  }
}
