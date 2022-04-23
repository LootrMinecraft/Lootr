package noobanidus.mods.lootr.networking;


import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class OpenCart implements IMessage {
  public int entityId;

  public OpenCart() {
    this.entityId = -1;
  }

  public void fromBytes(ByteBuf buf) {
    this.entityId = buf.readInt();
  }

  public OpenCart(int entityId) {
    this.entityId = entityId;
  }

  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.entityId);
  }
}

