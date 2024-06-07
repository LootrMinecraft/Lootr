package noobanidus.mods.lootr.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/* Shamelessly cribbed from Mekanism
Original source: https://github.com/mekanism/Mekanism/blob/1.20.4/src/main/java/mekanism/common/network/IMekanismPacket.java
 */
public interface ILootrPacket<CONTEXT extends IPayloadContext> extends CustomPacketPayload {

  void handle(CONTEXT context);

  default void handleMainThread(CONTEXT context) {
    context.workHandler().execute(() -> handle(context));
  }
}
