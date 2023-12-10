package noobanidus.mods.lootr.network;


import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.NetworkEvent;
import noobanidus.mods.lootr.network.client.ClientHandlers;

import java.util.function.Supplier;

public class OpenCart {
  public int entityId;

  public OpenCart(FriendlyByteBuf buffer) {
    this.entityId = buffer.readInt();
  }

  public OpenCart(int entityId) {
    this.entityId = entityId;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(this.entityId);
  }

  public void handle(NetworkEvent.Context context) {
    context.enqueueWork(() -> handle(this, context));
    context.setPacketHandled(true);
  }

  @OnlyIn(Dist.CLIENT)
  private static void handle(OpenCart message, NetworkEvent.Context context) {
    ClientHandlers.handleOpenCart(message, context);
  }
}

