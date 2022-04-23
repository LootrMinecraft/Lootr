package noobanidus.mods.lootr.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.networking.CloseCart;
import noobanidus.mods.lootr.networking.OpenCart;

public class ClientPacketHandlers {
  public static IMessage handleOpenCart(OpenCart message, MessageContext context) {
    Minecraft.getMinecraft().addScheduledTask(() -> {
      World world = Minecraft.getMinecraft().world;
      if (world == null) {
        Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as world is null.");
        return;
      }
      Entity cart = world.getEntityByID(message.entityId);
      if (cart == null) {
        Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as entity is null.");
        return;
      }

      if (!(cart instanceof LootrChestMinecartEntity)) {
        Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as entity is not a Lootr minecart.");
        return;
      }

      ((LootrChestMinecartEntity) cart).setOpened();
    });
    return null;
  }

  public static IMessage handleCloseCart(CloseCart message, MessageContext context) {
    Minecraft.getMinecraft().addScheduledTask(() -> {
      World world = Minecraft.getMinecraft().world;
      if (world == null) {
        Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as closed as world is null.");
        return;
      }
      Entity cart = world.getEntityByID(message.entityId);
      if (cart == null) {
        Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as closed as entity is null.");
        return;
      }

      if (!(cart instanceof LootrChestMinecartEntity)) {
        Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as closed as entity is not a Lootr minecart.");
        return;
      }

      ((LootrChestMinecartEntity) cart).setClosed();
    });
    return null;
  }
}
