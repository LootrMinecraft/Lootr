package noobanidus.mods.lootr.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;

@Mod.EventBusSubscriber(modid= Lootr.MODID)
public class HandleBreak {

  @SubscribeEvent
  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    PlayerEntity player = event.getPlayer();

    if (!event.getWorld().isClientSide()) {
      if (ModBlocks.LOOT_CONTAINERS.contains(event.getState().getBlock())) {
        if (ConfigManager.DISABLE_BREAK.get()) {
          if (player.abilities.instabuild) {
            if (!player.isShiftKeyDown()) {
              event.setCanceled(true);
              player.sendMessage(new TranslationTextComponent("lootr.message.cannot_break_sneak").setStyle(Style.EMPTY.withColor(TextFormatting.AQUA)), Util.NIL_UUID);
            }
          } else {
            event.setCanceled(true);
            player.sendMessage(new TranslationTextComponent("lootr.message.cannot_break").setStyle(Style.EMPTY.withColor(TextFormatting.AQUA)), Util.NIL_UUID);
          }
        } else {
          if (!player.isShiftKeyDown()) {
            event.setCanceled(true);
            player.sendMessage(new TranslationTextComponent("lootr.message.should_sneak").setStyle(Style.EMPTY.withColor(TextFormatting.AQUA)), Util.NIL_UUID);
            player.sendMessage(new TranslationTextComponent("lootr.message.should_sneak2", new TranslationTextComponent("lootr.message.should_sneak3").setStyle(Style.EMPTY.withColor(TextFormatting.AQUA).withBold(true))).setStyle(Style.EMPTY.withColor(TextFormatting.AQUA)), Util.NIL_UUID);
          }
        }
      }
    }
  }
}
