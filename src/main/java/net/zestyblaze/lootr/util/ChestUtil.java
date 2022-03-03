package net.zestyblaze.lootr.util;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.data.DataStorage;
import net.zestyblaze.lootr.registry.LootrAdvancementsInit;
import net.zestyblaze.lootr.registry.LootrStatsInit;

import java.util.Random;
import java.util.UUID;

public class ChestUtil {
    public static Random random = new Random();

    public static boolean handleLootSneak(Block block, Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) {
            return false;
        }
        if (player.isSpectator()) {
            return false;
        }

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof ILootBlockEntity tile) {
            if (tile.getOpeners().remove(player.getUUID())) {
                te.setChanged();
                tile.updatePacketViaState();
                //UpdateModelData message = new UpdateModelData(te.getBlockPos());
                //PacketHandler.sendToInternal(message, (ServerPlayer) player);
            }
            return true;
        }

        return false;
    }

    public static boolean handleLootChest(Block block, Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) {
            return false;
        }
        if (player.isSpectator()) {
            player.openMenu(null);
            return false;
        }
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof ILootBlockEntity tile) {
            UUID tileId = tile.getTileId();
            if (DataStorage.isDecayed(tileId)) {
                level.destroyBlock(pos, true);
                player.sendMessage(new TranslatableComponent("lootr.message.decayed").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true)), Util.NIL_UUID);
                DataStorage.removeDecayed(tileId);
                return false;
            } else {
                int decayValue = DataStorage.getDecayValue(tileId);
                if (decayValue > 0) {
                    player.sendMessage(new TranslatableComponent("lootr.message.decay_in", decayValue / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true)), Util.NIL_UUID);
                } else if (decayValue == -1) {
                    /*
                    if (ConfigManager.isDecaying((ServerLevel)level, (ILootBlockEntity)te)) {
                        DataStorage.setDecaying(tileId, ConfigManager.DECAY_VALUE.get());
                        player.sendMessage(new TranslatableComponent("lootr.message.decay_start", ConfigManager.DECAY_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true)), Util.NIL_UUID);
                    }

                     */
                }
            }
            if (block instanceof BarrelBlock) {
                LootrAdvancementsInit.BARREL_PREDICATE.trigger((ServerPlayer) player, ((ILootBlockEntity) te).getTileId());
            } else if (block instanceof ChestBlock) {
                LootrAdvancementsInit.CHEST_PREDICATE.trigger((ServerPlayer) player, ((ILootBlockEntity) te).getTileId());
            } /* else if (block instanceof LootrShulkerBlock) {
                LootrAdvancementsInit.SHULKER_PREDICATE.trigger((ServerPlayer) player, ((ILootBlockEntity) te).getTileId());
            }
            */
            if (DataStorage.isRefreshed(tileId)) {
                DataStorage.refreshInventory(level, pos, ((ILootBlockEntity) te).getTileId(), (ServerPlayer) player);
                DataStorage.removeRefreshed(tileId);
                player.sendMessage(new TranslatableComponent("lootr.message.refreshed").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), Util.NIL_UUID);
            } else {
                int refreshValue = DataStorage.getRefreshValue(tileId);
                if (refreshValue > 0) {
                    player.sendMessage(new TranslatableComponent("lootr.message.refresh_in", refreshValue / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), Util.NIL_UUID);
                } else if (refreshValue == -1) {
                    /*
                    if (ConfigManager.isRefreshing((ServerLevel) level, (ILootBlockEntity) te)) {
                        DataStorage.setRefreshing(tileId, ConfigManager.REFRESH_VALUE.get());
                        player.sendMessage(new TranslatableComponent("lootr.message.refresh_start", ConfigManager.REFRESH_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), Util.NIL_UUID);
                    }

                     */
                }
            }
            MenuProvider provider = DataStorage.getInventory(level, ((ILootBlockEntity) te).getTileId(), pos, (ServerPlayer) player, (RandomizableContainerBlockEntity) te, ((ILootBlockEntity) te)::unpackLootTable);
            if (!DataStorage.isScored(player.getUUID(), ((ILootBlockEntity)te).getTileId())) {
                player.awardStat(LootrStatsInit.LOOTED_STAT);
                LootrAdvancementsInit.SCORE_PREDICATE.trigger((ServerPlayer) player, null);
                DataStorage.score(player.getUUID(), ((ILootBlockEntity) te).getTileId());
            }
            if (tile.getOpeners().add(player.getUUID())) {
                te.setChanged();
                tile.updatePacketViaState();
            }
            player.openMenu(provider);
            PiglinAi.angerNearbyPiglins(player, true);
            return true;
        } else {
            return false;
        }
    }
}
