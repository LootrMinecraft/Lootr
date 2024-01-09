package noobanidus.mods.lootr.registry;

import net.minecraft.world.entity.Entity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.network.NetworkConstants;
import noobanidus.mods.lootr.util.PlatformUtils;

public class LootrNetworkingInit {
    public static void registerClientNetwork() {
        PlatformUtils.registerClientNetworkReceiver(NetworkConstants.CLOSE_CART_CHANNEL, (client, handler, buf, responseSender) -> {
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

        PlatformUtils.registerClientNetworkReceiver(NetworkConstants.OPEN_CART_CHANNEL, (client, handler, buf, responseSender) -> {
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
