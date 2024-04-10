package noobanidus.mods.lootr.network;


import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import noobanidus.mods.lootr.network.client.ClientHandlers;

import java.util.function.Supplier;

public class UpdateModelData {
  public BlockPos pos;

  public UpdateModelData(FriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
  }

  public UpdateModelData(BlockPos pos) {
    this.pos = pos;
  }

  @OnlyIn(Dist.CLIENT)
  private static void handle(UpdateModelData message, Supplier<NetworkEvent.Context> context) {
    ClientHandlers.handleUpdateModel(message, context);
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
  }

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> handle(this, context));
    context.get().setPacketHandled(true);
  }
}

