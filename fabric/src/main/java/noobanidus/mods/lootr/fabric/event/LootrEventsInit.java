package noobanidus.mods.lootr.fabric.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import noobanidus.mods.lootr.common.chunk.LoadedChunks;
import noobanidus.mods.lootr.common.command.CommandLootr;
import noobanidus.mods.lootr.common.data.DataStorage;

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

    ServerTickEvents.END_SERVER_TICK.register(server -> {
      DataStorage.doTick();
      BlockEntityTicker.onServerTick(server);
    });

    ServerChunkEvents.CHUNK_LOAD.register(LoadedChunks::onChunkLoad);
    ServerChunkEvents.CHUNK_UNLOAD.register(LoadedChunks::onChunkUnload);

    PlayerBlockBreakEvents.BEFORE.register(HandleBreak::beforeBlockBreak);

    PlayerBlockBreakEvents.CANCELED.register(HandleBreak::afterBlockBreak);

    CommandRegistrationCallback.EVENT.register((dispatcher, reg, env) -> {
      CommandLootr.register(dispatcher);
    });

    ModContainer container = FabricLoader.getInstance().getModContainer(LootrAPI.MODID).orElseThrow();

    ResourceManagerHelper.registerBuiltinResourcePack(LootrAPI.rl("old_textures"), container, Component.literal("Lootr - Old Textures"), ResourcePackActivationType.NORMAL);
    registerPack(container, "lootr_no_advancements", Component.literal("Disable Lootr Advancements"));
    registerPack(container, "lootr_no_suspicious_blocks", Component.literal("Disable Lootr Converting Suspicious Blocks"));
  }

  private static void registerPack (ModContainer container, String name, Component desc) {
    ResourceManagerHelperImpl.registerBuiltinResourcePack(
        ResourceLocation.fromNamespaceAndPath(container.getMetadata().getId(), name),
        "datapacks/" + name,
        container,
        desc,
        ResourcePackActivationType.NORMAL);
  }
}
