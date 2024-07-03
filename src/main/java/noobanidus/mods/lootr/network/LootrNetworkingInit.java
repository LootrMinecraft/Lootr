package noobanidus.mods.lootr.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Entity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

public class LootrNetworkingInit {
    public static void registerClientNetwork() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.CLOSE_CART_CHANNEL, (client, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            client.execute(() -> {
                if (client.player != null && client.player.level() != null) {
                    Entity potential = client.player.level().getEntity(entityId);
                    if (potential instanceof LootrChestMinecartEntity cart) {
                        cart.setClosed();
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.OPEN_CART_CHANNEL, (client, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            client.execute(() -> {
                if (client.player != null && client.player.level() != null) {
                    Entity potential = client.player.level().getEntity(entityId);
                    if (potential instanceof LootrChestMinecartEntity cart) {
                        cart.setOpened();
                    }
                }
            });
        });
    }
}
