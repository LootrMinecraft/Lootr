package noobanidus.mods.lootr.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.entities.TileTicker;
import noobanidus.mods.lootr.event.HandleChunk;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.EntityTicker;
import noobanidus.mods.lootr.LootrTags;

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

    PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
      if (!world.isClientSide()) {
        if (state.is(LootrTags.Blocks.CONTAINERS)) {
          if ((LootrAPI.isFakePlayer(player) && ConfigManager.get().breaking.enable_fake_player_break) || ConfigManager.get().breaking.enable_break) {
            return true;
          }

          if (ConfigManager.get().breaking.disable_break) {
            if (player.getAbilities().instabuild) {
              if (!player.isShiftKeyDown()) {
                player.sendSystemMessage(Component.translatable("lootr.message.cannot_break_sneak").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))));
                return false;
              }
            } else {
              player.sendSystemMessage(Component.translatable("lootr.message.cannot_break").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))));
              return false;
            }
          } else {
            if (!player.isShiftKeyDown()) {
              player.sendSystemMessage(Component.translatable("lootr.message.should_sneak").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))));
              player.sendSystemMessage(Component.translatable("lootr.message.should_sneak2", Component.translatable("lootr.message.should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))));
              return false;
            }
          }
        }
      }
      return true;
    });

    PlayerBlockBreakEvents.CANCELED.register((world, player, pos, state, blockEntity) -> {
      if (state.is(LootrTags.Blocks.CONTAINERS)) {
        blockEntity.setChanged();
        if (blockEntity instanceof ILootBlockEntity lbe) {
          lbe.updatePacketViaState();
        }
      }
    });
  }
}
