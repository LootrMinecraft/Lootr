package noobanidus.mods.lootr.fabric.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.config.LootrConfig;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import noobanidus.mods.lootr.common.chunk.LoadedChunks;
import noobanidus.mods.lootr.common.command.CommandLootr;
import noobanidus.mods.lootr.fabric.network.to_client.PacketSyncConfig;

public class LootrEventsInit {
  public static MinecraftServer serverInstance;

  public static void registerEvents() {
    ServerLifecycleEvents.SERVER_STARTING.register(server -> {
      serverInstance = server;
      LoadedChunks.clear();
    });

    ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
      serverInstance = null;
      LoadedChunks.clear();
    });

    ServerTickEvents.END_SERVER_TICK.register(BlockEntityTicker::onServerTick);

    ServerChunkEvents.CHUNK_LOAD.register(LoadedChunks::onChunkLoad);
    ServerChunkEvents.CHUNK_UNLOAD.register(LoadedChunks::onChunkUnload);

    PlayerBlockBreakEvents.BEFORE.register(HandleBreak::beforeBlockBreak);

    CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> {
      CommandLootr.register(dispatcher);
    });

    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
      if (server.isDedicatedServer()) {
        ServerPlayNetworking.send(handler.player, new PacketSyncConfig(LootrConfig.getConfigForSync()));
      }
    });

    ModContainer container = FabricLoader.getInstance().getModContainer(LootrAPI.MODID).orElseThrow();

    registerPack(container, "lootr_no_advancements", Component.literal("Disable Lootr Advancements"));
    registerPack(container, "lootr_no_suspicious_blocks", Component.literal("Disable Lootr Converting Suspicious Blocks"));
  }

  private static void registerPack(ModContainer container, String name, Component desc) {
    ResourceLoader.registerBuiltinPack(
        Identifier.fromNamespaceAndPath(container.getMetadata().getId(), name),
        container,
        desc,
        PackActivationType.NORMAL);
  }
}
