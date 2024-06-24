package noobanidus.mods.lootr.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.BlockEvent;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.ConfigManager;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleBreak {

  @SubscribeEvent
  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    Player player = event.getPlayer();

    if (!event.getLevel().isClientSide()) {
      if (event.getState().is(LootrTags.Blocks.CONTAINERS)) {
        if ((player instanceof FakePlayer && ConfigManager.ENABLE_FAKE_PLAYER_BREAK.get()) || ConfigManager.ENABLE_BREAK.get()) {
          return;
        }
        if (ConfigManager.DISABLE_BREAK.get()) {
          if (player.getAbilities().instabuild) {
            if (!player.isShiftKeyDown()) {
              event.setCanceled(true);
              player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak").setStyle(getChatStyle()), false);
            }
          } else {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break").setStyle(getChatStyle()), false);
          }
        } else {
          if (!event.getPlayer().isShiftKeyDown()) {
            event.setCanceled(true);
            event.getPlayer().displayClientMessage(Component.translatable("lootr.message.should_sneak").setStyle(getChatStyle()), false);
            event.getPlayer().displayClientMessage(Component.translatable("lootr.message.should_sneak2", Component.translatable("lootr.message.should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(getChatStyle()), false);
          }
        }
      }
    }
  }

  public static Style getChatStyle() {
    if (ConfigManager.DISABLE_MESSAGE_STYLES.get()) {
      return Style.EMPTY;
    }

    return Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA));
  }
}
