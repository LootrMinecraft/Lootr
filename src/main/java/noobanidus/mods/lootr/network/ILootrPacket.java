package noobanidus.mods.lootr.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/* Shamelessly cribbed from Mekanism
Original source: https://github.com/mekanism/Mekanism/blob/1.21.x/src/main/java/mekanism/common/network/IMekanismPacket.java
 */
public interface ILootrPacket extends CustomPacketPayload {

  void handle(IPayloadContext context);
}
