package noobanidus.mods.lootr.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.networking.CloseCart;
import noobanidus.mods.lootr.networking.OpenCart;

import java.util.function.Supplier;

public class ClientHandlers {
  public static void handleOpenCart(OpenCart message, Supplier<NetworkEvent.Context> context) {
    Level world = Minecraft.getInstance().level;
    if (world == null) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as world is null.");
      context.get().setPacketHandled(true);
      return;
    }
    Entity cart = world.getEntity(message.entityId);
    if (cart == null) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as entity is null.");
      context.get().setPacketHandled(true);
      return;
    }

    if (!(cart instanceof LootrChestMinecartEntity)) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as entity is not a Lootr minecart.");
      context.get().setPacketHandled(true);
      return;
    }

    ((LootrChestMinecartEntity) cart).setOpened();
  }

  public static void handleCloseCart (CloseCart message, Supplier<NetworkEvent.Context> context) {
    Level world = Minecraft.getInstance().level;
    if (world == null) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as closed as world is null.");
      context.get().setPacketHandled(true);
      return;
    }
    Entity cart = world.getEntity(message.entityId);
    if (cart == null) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as closed as entity is null.");
      context.get().setPacketHandled(true);
      return;
    }

    if (!(cart instanceof LootrChestMinecartEntity)) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as closed as entity is not a Lootr minecart.");
      context.get().setPacketHandled(true);
      return;
    }

    ((LootrChestMinecartEntity) cart).setClosed();
  }
}
