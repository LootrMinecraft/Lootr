package noobanidus.mods.lootr.networking;


import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import noobanidus.mods.lootr.client.ClientPacketHandlers;

import java.util.function.Supplier;

public class CloseCart implements IMessage {
  public int entityId;

  public CloseCart() {
    this.entityId = -1;
  }

  public void fromBytes(ByteBuf buf) {
    this.entityId = buf.readInt();
  }

  public CloseCart(int entityId) {
    this.entityId = entityId;
  }

  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.entityId);
  }
}

