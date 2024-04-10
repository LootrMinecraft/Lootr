package noobanidus.mods.lootr.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import noobanidus.mods.lootr.network.client.ClientHandlers;

import java.util.function.Supplier;

public class CloseCart {
  public int entityId;

  public CloseCart(FriendlyByteBuf buffer) {
    this.entityId = buffer.readInt();
  }

  public CloseCart(int entityId) {
    this.entityId = entityId;
  }

  @OnlyIn(Dist.CLIENT)
  private static void handle(CloseCart message, Supplier<NetworkEvent.Context> context) {
    ClientHandlers.handleCloseCart(message, context);
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(this.entityId);
  }

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> handle(this, context));
    context.get().setPacketHandled(true);
  }
}

