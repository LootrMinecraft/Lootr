package net.zestyblaze.lootr.registry;

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
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.blocks.entities.TileTicker;
import net.zestyblaze.lootr.chunk.HandleChunk;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.entity.EntityTicker;

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
                if (LootrBlockInit.specialLootChests.contains(state.getBlock())) {
                    // TODO: Entity tags
                    if (LootrModConfig.get().breaking.enable_break) {
                        return true;
                    }

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
            }
            return true;
        });

        PlayerBlockBreakEvents.CANCELED.register((world, player, pos, state, blockEntity) -> {
            if(LootrBlockInit.specialLootChests.contains(state.getBlock())) {
                blockEntity.setChanged();
                if (blockEntity instanceof ILootBlockEntity lbe) {
                    lbe.updatePacketViaState();
                }
            }
        });
    }
}
