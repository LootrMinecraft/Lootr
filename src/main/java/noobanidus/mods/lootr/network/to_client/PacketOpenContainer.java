package noobanidus.mods.lootr.network.to_client;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.network.ILootrPacket;
import noobanidus.mods.lootr.network.client.ClientHandlers;

public record PacketOpenContainer(BlockPos position) implements ILootrPacket<PlayPayloadContext> {
  public static final ResourceLocation ID = Lootr.rl("open_container");

  public PacketOpenContainer(FriendlyByteBuf buffer) {
    this(buffer.readBlockPos());
  }

  @Override
  public void handle(PlayPayloadContext context) {
    ClientHandlers.handleOpenContainer(this.position);
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(this.position);
  }

  @Override
  public ResourceLocation id() {
    return ID;
  }
}
