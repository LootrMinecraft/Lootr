package noobanidus.mods.lootr.event;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;

@Mod.EventBusSubscriber(modid= LootrAPI.MODID)
public class HandleBreak {

  @SubscribeEvent
  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    Player player = event.getPlayer();

    if (!event.getLevel().isClientSide()) {
      if (ModBlocks.getSpecialLootChests().contains(event.getState().getBlock())) {
        if ((player instanceof FakePlayer && ConfigManager.ENABLE_FAKE_PLAYER_BREAK.get()) || ConfigManager.ENABLE_BREAK.get()) {
          return;
        }
        if (ConfigManager.DISABLE_BREAK.get()) {
          if (player.getAbilities().instabuild) {
            if (!player.isShiftKeyDown()) {
              event.setCanceled(true);
              player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), false);
            }
          } else {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), false);
          }
        } else {
          if (!event.getPlayer().isShiftKeyDown()) {
            event.setCanceled(true);
            event.getPlayer().displayClientMessage(Component.translatable("lootr.message.should_sneak").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), false);
            event.getPlayer().displayClientMessage(Component.translatable("lootr.message.should_sneak2", Component.translatable("lootr.message.should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), false);
          }
        }
      }
    }
  }
}
