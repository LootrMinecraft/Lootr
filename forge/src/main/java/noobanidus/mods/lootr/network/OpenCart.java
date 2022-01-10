package noobanidus.mods.lootr.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
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

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> handle(this, context));
    context.get().setPacketHandled(true);
  }

  @OnlyIn(Dist.CLIENT)
  private static void handle(OpenCart message, Supplier<NetworkEvent.Context> context) {
    ClientHandlers.handleOpenCart(message, context);
  }
}

