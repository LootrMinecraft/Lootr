package noobanidus.mods.lootr.network;


import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.NetworkEvent;
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

  public void encode(FriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
  }

  public void handle(NetworkEvent.Context context) {
    context.enqueueWork(() -> handle(this, context));
    context.setPacketHandled(true);
  }

  @OnlyIn(Dist.CLIENT)
  private static void handle(UpdateModelData message, NetworkEvent.Context context) {
    ClientHandlers.handleUpdateModel(message, context);
  }
}

