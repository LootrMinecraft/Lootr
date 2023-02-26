package noobanidus.mods.lootr.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.entities.TileTicker;
import noobanidus.mods.lootr.event.HandleChunk;
import noobanidus.mods.lootr.config.LootrModConfig;
import noobanidus.mods.lootr.entity.EntityTicker;

public class LootrEventsInit {
  public static MinecraftServer serverInstance;

  public static void registerEvents() {
    ServerLifecycleEvents.SERVER_STARTING.register(server -> {
      serverInstance = server;
      HandleChunk.onServerStarted();
    });

    ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
      serverInstance = null;
    });

    ServerTickEvents.END_SERVER_TICK.register(server -> {
      EntityTicker.serverTick();
      TileTicker.serverTick();
    });

    ServerChunkEvents.CHUNK_LOAD.register(HandleChunk::onChunkLoad);

    // TODO: Check to see if this properly cancels block breaking
    PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
      if (LootrBlockInit.specialLootChests.contains(state.getBlock())) {
        if (LootrModConfig.get().breaking.disable_break) {
          if (player.getAbilities().instabuild) {
            if (!player.isShiftKeyDown()) {
              player.sendMessage(new TranslatableComponent("lootr.message.cannot_break_sneak").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), Util.NIL_UUID);
              return false;
            }
          } else {
            player.sendMessage(new TranslatableComponent("lootr.message.cannot_break").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), Util.NIL_UUID);
            return false;
          }
        } else {
          if (!player.isShiftKeyDown()) {
            player.sendMessage(new TranslatableComponent("lootr.message.should_sneak").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), Util.NIL_UUID);
            player.sendMessage(new TranslatableComponent("lootr.message.should_sneak2", new TranslatableComponent("lootr.message.should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), Util.NIL_UUID);
            return false;
          }
        }
      }
      return true;
    });

    PlayerBlockBreakEvents.CANCELED.register((world, player, pos, state, blockEntity) -> {
      if (LootrBlockInit.specialLootChests.contains(state.getBlock())) {
        blockEntity.setChanged();
        if (blockEntity instanceof ILootBlockEntity lbe) {
          lbe.updatePacketViaState();
        }
      }
    });
  }
}
