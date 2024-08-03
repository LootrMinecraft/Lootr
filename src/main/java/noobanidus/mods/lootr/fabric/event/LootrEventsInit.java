package noobanidus.mods.lootr.fabric.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import noobanidus.mods.lootr.common.command.CommandLootr;
import noobanidus.mods.lootr.common.data.DataStorage;
import noobanidus.mods.lootr.common.entity.EntityTicker;

public class LootrEventsInit {
  public static MinecraftServer serverInstance;
  public static CommandLootr lootrCommand;

  public static void registerEvents() {
    ServerLifecycleEvents.SERVER_STARTING.register(server -> {
      serverInstance = server;
      HandleChunk.onServerStarted();
    });

    ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
      serverInstance = null;
    });

    ServerTickEvents.END_SERVER_TICK.register(server -> {
      DataStorage.doTick();
      BlockEntityTicker.onServerTick();
      EntityTicker.onServerTick();
    });

    ServerChunkEvents.CHUNK_LOAD.register(HandleChunk::onChunkLoad);

    PlayerBlockBreakEvents.BEFORE.register(HandleBreak::beforeBlockBreak);

    PlayerBlockBreakEvents.CANCELED.register(HandleBreak::afterBlockBreak);

    CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> {
      lootrCommand = new CommandLootr(dispatcher);
      lootrCommand.register();
    });
  }
}
