package noobanidus.mods.lootr.neoforge.network.to_server;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.neoforge.network.to_client.PacketCloseContainer;
import noobanidus.mods.lootr.neoforge.network.to_client.PacketOpenContainer;

public class ServerHandlers {
  public static void handleRequestUpdate(IPayloadContext context, GlobalPos pos) {
    ServerLevel level = context.player().level().getServer().getLevel(pos.dimension());
    if (level == null) {
      return;
    }

    BlockEntity blockEntity = level.getBlockEntity(pos.pos());
    if (blockEntity == null) {
      return;
    }

    ILootrBlockEntity resolved = LootrAPI.wrapBlockEntity(blockEntity);
    if (resolved == null) {
      return;
    }

    Packet<?> packet = blockEntity.getUpdatePacket();
    if (packet != null) {
      context.connection().send(packet);
    }

    if (resolved.hasVisualOpened(context.player())) {
      PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new PacketOpenContainer(pos.pos()));
    } else {
      PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new PacketCloseContainer(pos.pos()));
    }
  }
}
