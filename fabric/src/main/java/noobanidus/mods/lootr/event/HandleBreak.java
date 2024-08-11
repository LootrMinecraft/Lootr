package noobanidus.mods.lootr.event;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.api.LootrTags;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

public class HandleBreak {
  public static boolean beforeBlockBreak(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
    if (world.isClientSide() || !state.is(LootrTags.Blocks.CONTAINERS)) {
      return true;
    }

    if (LootrAPI.isFakePlayer(player) && LootrAPI.isFakePlayerBreakEnabled() || LootrAPI.isBreakEnabled()) {
      return true;
    }

    if (!world.isClientSide()) {
      if (state.is(LootrTags.Blocks.CONTAINERS)) {
        if ((player instanceof FakePlayer && ConfigManager.get().breaking.enable_fake_player_break) || ConfigManager.get().breaking.enable_break) {
          return true;
        }
        if (ConfigManager.get().breaking.disable_break) {
          if (player.getAbilities().instabuild) {
            if (!player.isShiftKeyDown()) {
              player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak").setStyle(getChatStyle()), false);
            }
          } else {
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break").setStyle(getChatStyle()), false);
          }
        } else {
          if (!player.isShiftKeyDown()) {
            player.displayClientMessage(Component.translatable("lootr.message.should_sneak").setStyle(getChatStyle()), false);
            player.displayClientMessage(Component.translatable("lootr.message.should_sneak2", Component.translatable("lootr.message.should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(getChatStyle()), false);
          }
        }
      } else {
        player.displayClientMessage(Component.translatable("lootr.message.cannot_break").setStyle(getChatStyle()), false);
        return false;
      }
    } else {
      if (!player.isShiftKeyDown()) {
        player.displayClientMessage(Component.translatable("lootr.message.should_sneak").setStyle(getChatStyle()), false);
        player.displayClientMessage(Component.translatable("lootr.message.should_sneak2", Component.translatable("lootr.message.should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(getChatStyle()), false);
        return false;
      }
    }

    return true;
  }

  public static void afterBlockBreak(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
    if (state.is(LootrTags.Blocks.CONTAINERS)) {
      blockEntity.setChanged();
      if (blockEntity instanceof ILootrBlockEntity lbe) {
        lbe.updatePacketViaForce(blockEntity);
      }
    }
  }

  public static Style getChatStyle() {
    if (ConfigManager.get().notifications.disable_message_styles) {
      return Style.EMPTY;
    }

    return Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA));
  }
}
