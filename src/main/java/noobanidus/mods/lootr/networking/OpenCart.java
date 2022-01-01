package noobanidus.mods.lootr.networking;


import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import noobanidus.mods.lootr.client.ClientPacketHandlers;

import java.util.function.Supplier;

public class OpenCart {
  public int entityId;

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
    context.get().setPacketHandled(true);
  }

  @OnlyIn(Dist.CLIENT)
  private static void handle(OpenCart message, Supplier<NetworkEvent.Context> context) {
    ClientPacketHandlers.handleOpenCart(message, context);
  }
}

