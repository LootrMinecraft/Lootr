package noobanidus.mods.lootr.networking;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import java.util.function.Supplier;

public class OpenCart {
  private int entityId;

  public OpenCart(PacketBuffer buffer) {
    this.entityId = buffer.readInt();
  }

  public OpenCart(int entityId) {
    this.entityId = entityId;
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(this.entityId);
  }

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> handle(this, context));
  }

  private static void handle(OpenCart message, Supplier<NetworkEvent.Context> context) {
    World world = Minecraft.getInstance().world;
    if (world == null) {
      Lootr.LOG.info("Unable to mark entity with id '" + message.entityId + "' as opened as world is null.");
      context.get().setPacketHandled(true);
      return;
    }
    Entity cart = world.getEntityByID(message.entityId);
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

    context.get().setPacketHandled(true);
  }
}

