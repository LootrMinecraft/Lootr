package noobanidus.mods.lootr.fabric.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.fabric.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

public class HandleBreak {
  public static boolean beforeBlockBreak(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
    if (world.isClientSide() || !state.is(LootrTags.Blocks.CONTAINERS)) {
      return true;
    }

    if (!(LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity lbe)) {
      return true;
    }
    if (!lbe.hasLootTable() && !lbe.isInfoReferenceInventory()) {
      return true;
    }
    if (LootrAPI.canDestroyOrBreak(player)) {
      return true;
    }

    boolean dumped = false;
    if (LootrAPI.breakToDropLoot() && !player.isShiftKeyDown()) {
      LootrAPI.dumpPlayerLoot(lbe, (ServerPlayer) player, (ServerLevel) world);
      dumped = true;
    }

    if (LootrAPI.isBreakDisabled()) {
      if (player.getAbilities().instabuild) {
        if (!player.isShiftKeyDown()) {
          if (!dumped) {
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak")
                .setStyle(getChatStyle()), false);
          }
          return false;
        }
      } else {
        if (!dumped) {
          player.displayClientMessage(Component.translatable("lootr.message.cannot_break")
              .setStyle(getChatStyle()), false);
        }
        return false;
      }
    } else {
      if (!player.isShiftKeyDown()) {
        if (!dumped) {
          player.displayClientMessage(Component.translatable("lootr.message.should_sneak")
              .setStyle(getChatStyle()), false);
          player.displayClientMessage(Component.translatable("lootr.message.should_sneak2")
              .setStyle(getChatStyle()), false);
        }
        return false;
      }
    }

    return true;
  }

  public static Style getChatStyle() {
    if (ConfigManager.get().notifications.disable_message_styles) {
      return Style.EMPTY;
    }

    return Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA));
  }
}
