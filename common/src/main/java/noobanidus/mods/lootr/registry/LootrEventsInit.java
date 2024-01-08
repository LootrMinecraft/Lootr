package noobanidus.mods.lootr.registry;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.blocks.entities.TileTicker;
import noobanidus.mods.lootr.chunk.HandleChunk;
import noobanidus.mods.lootr.config.LootrModConfig;
import noobanidus.mods.lootr.entity.EntityTicker;
import noobanidus.mods.lootr.util.PlatformUtils;

public class LootrEventsInit {
    public static MinecraftServer serverInstance;

    public static void registerEvents() {
        PlatformUtils.registerServerStartEvent(server -> {
            serverInstance = server;
            HandleChunk.onServerStarted();
        });

        PlatformUtils.registerServerStoppedEvent(server -> {
            serverInstance = null;
        });

        PlatformUtils.registerServerEndTickEvent(server -> {
            EntityTicker.serverTick();
            TileTicker.serverTick();
        });

        PlatformUtils.registerServerChunkLoadEvent(HandleChunk::onChunkLoad);

        PlatformUtils.registerBeforePlayerBlockBreakEvent((world, player, pos, state, blockEntity) -> {
            if (!world.isClientSide()) {
                if (state.is(LootrTags.Blocks.CONTAINERS)) {
                    if ((LootrAPI.isFakePlayer(player) && LootrModConfig.get().breaking.enable_fake_player_break) || LootrModConfig.get().breaking.enable_break) {
                        return true;
                    }

                    if (LootrModConfig.get().breaking.disable_break) {
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

        PlatformUtils.registerPlayerBlockBreakCanceledEvent((world, player, pos, state, blockEntity) -> {
            if (state.is(LootrTags.Blocks.CONTAINERS)) {
                blockEntity.setChanged();
                if (blockEntity instanceof ILootBlockEntity lbe) {
                    lbe.updatePacketViaState();
                }
            }
        });
    }
}
