package noobanidus.mods.lootr.network.to_client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.network.ILootrPacket;
import noobanidus.mods.lootr.network.client.ClientHandlers;

public record PacketCloseCart(int entityId) implements ILootrPacket<PlayPayloadContext> {
  public static final ResourceLocation ID = Lootr.rl("close_cart");

  public PacketCloseCart(FriendlyByteBuf buffer) {
    this(buffer.readVarInt());
  }

  @Override
  public void handle(PlayPayloadContext context) {
    ClientHandlers.handleCloseCart(this.entityId);
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeVarInt(this.entityId);
  }

  @Override
  public ResourceLocation id() {
    return ID;
  }
}
