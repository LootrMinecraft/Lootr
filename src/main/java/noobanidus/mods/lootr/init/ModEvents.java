package noobanidus.mods.lootr.init;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.entities.TileTicker;
import noobanidus.mods.lootr.command.CommandLootr.CommandLootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.EntityTicker;
import noobanidus.mods.lootr.event.HandleChunk;

public class ModEvents {
  public static MinecraftServer serverInstance;

  public static void register() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
      dispatcher.register(CommandLootr.builder(Commands.literal("lootr").requires(p -> p.hasPermission(2))));
    });

    ServerLifecycleEvents.SERVER_STARTING.register(server -> {
      Lootr.serverAccess.setServer(server);
      HandleChunk.onServerStarted();
    });

    ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
      Lootr.serverAccess.setServer(null);
      serverInstance = null;
    });

    ServerTickEvents.END_SERVER_TICK.register(server -> {
      EntityTicker.serverTick();
      TileTicker.serverTick();
    });

    ServerChunkEvents.CHUNK_LOAD.register(HandleChunk::onChunkLoad);

    PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
      if (ModBlocks.specialLootChests.contains(state.getBlock())) {
        if (ConfigManager.get().breaking.disable_break) {
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
      if (ModBlocks.specialLootChests.contains(state.getBlock())) {
        blockEntity.setChanged();
        if (blockEntity instanceof ILootBlockEntity lbe) {
          lbe.updatePacketViaState();
        }
      }
    });
  }
}
