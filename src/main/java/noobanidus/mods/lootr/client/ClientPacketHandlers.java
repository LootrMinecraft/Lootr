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
    Lootr.proxy.changeCartStatus(message.entityId, true);
    return null;
  }

  public static IMessage handleCloseCart(CloseCart message, MessageContext context) {
    Lootr.proxy.changeCartStatus(message.entityId, false);
    return null;
  }
}
