package noobanidus.mods.lootr.fabric.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.fabric.network.to_client.PacketCloseContainer;
import noobanidus.mods.lootr.fabric.network.to_client.PacketOpenContainer;
import noobanidus.mods.lootr.fabric.network.to_server.PacketRequestUpdate;

public class LootrNetworkingInit {
  public static void register() {
    ServerPlayNetworking.registerGlobalReceiver(PacketRequestUpdate.TYPE, (payload, context) -> {
      context.server().execute(() -> {
        GlobalPos pos = payload.position();
        ServerLevel level = context.server().getLevel(pos.dimension());
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
          context.player().connection.send(packet);
        }

        if (resolved.hasVisualOpened(context.player())) {
          ServerPlayNetworking.send(context.player(), new PacketOpenContainer(pos.pos()));
        } else {
          ServerPlayNetworking.send(context.player(), new PacketCloseContainer(pos.pos()));
        }
      });
    });
  }
}
