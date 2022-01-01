package noobanidus.mods.lootr.networking;


import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import noobanidus.mods.lootr.client.ClientPacketHandlers;

import java.util.function.Supplier;

public class CloseCart {
  public int entityId;

  public CloseCart(PacketBuffer buffer) {
    this.entityId = buffer.readInt();
  }

  public CloseCart(int entityId) {
    this.entityId = entityId;
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(this.entityId);
  }

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> handle(this, context));
    context.get().setPacketHandled(true);
  }

  @OnlyIn(Dist.CLIENT)
  private static void handle(CloseCart message, Supplier<NetworkEvent.Context> context) {
    ClientPacketHandlers.handleCloseCart(message, context);
  }
}

