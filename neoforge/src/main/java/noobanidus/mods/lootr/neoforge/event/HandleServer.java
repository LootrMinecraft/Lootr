package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.config.LootrConfig;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import noobanidus.mods.lootr.neoforge.network.to_client.PacketSyncConfig;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleServer {
  @SubscribeEvent
  public static void onServerTick(ServerTickEvent.Post event) {
    BlockEntityTicker.onServerTick(event.getServer());
  }

  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    Player player = event.getEntity();
    if (player.level().getServer() == null) {
      return;
    } else {
      if (player.level().getServer().isDedicatedServer()) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new PacketSyncConfig(LootrConfig.getConfigForSync()));
      }
    }
  }
}
