package noobanidus.mods.lootr.fabric.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import noobanidus.mods.lootr.common.command.CommandLootr;
import noobanidus.mods.lootr.common.data.DataStorage;
import noobanidus.mods.lootr.common.entity.EntityTicker;
import noobanidus.mods.lootr.common.event.HandleChunk;

public class LootrEventsInit {
  public static MinecraftServer serverInstance;
  public static CommandLootr lootrCommand;

  public static void registerEvents() {
    ServerLifecycleEvents.SERVER_STARTING.register(server -> {
      serverInstance = server;
      HandleChunk.clear();
    });

    ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
      serverInstance = null;
      HandleChunk.clear();
    });

    ServerTickEvents.END_SERVER_TICK.register(server -> {
      DataStorage.doTick();
      BlockEntityTicker.onServerTick();
      EntityTicker.onServerTick();
    });

    ServerChunkEvents.CHUNK_LOAD.register((serverLevel, levelChunk) -> {
      if (levelChunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL)) {
        HandleChunk.addLoadedChunk(serverLevel.dimension(), levelChunk.getPos());
      }
    });

    PlayerBlockBreakEvents.BEFORE.register(HandleBreak::beforeBlockBreak);

    PlayerBlockBreakEvents.CANCELED.register(HandleBreak::afterBlockBreak);

    CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> {
      lootrCommand = new CommandLootr(dispatcher);
      lootrCommand.register();
    });

    ModContainer container = FabricLoader.getInstance().getModContainer(LootrAPI.MODID).orElseThrow();

    ResourceManagerHelper.registerBuiltinResourcePack(LootrAPI.rl("old_textures"), container, Component.literal("Lootr - Old Textures"), ResourcePackActivationType.NORMAL);
  }
}
