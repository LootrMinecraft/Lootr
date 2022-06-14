package noobanidus.mods.lootr.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Util;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class HandleBreak {

  @SubscribeEvent
  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    EntityPlayer player = event.getPlayer();

    if (!event.getWorld().isRemote) {
      if (ModBlocks.LOOT_CONTAINERS.contains(event.getState().getBlock())) {
        if (ConfigManager.DISABLE_BREAK) {
          if (player.capabilities.isCreativeMode) {
            if (!player.isSneaking()) {
              event.setCanceled(true);
              player.sendMessage(new TextComponentTranslation("lootr.message.cannot_break_sneak").setStyle(new Style().setColor(TextFormatting.AQUA)));
            }
          } else {
            event.setCanceled(true);
            player.sendMessage(new TextComponentTranslation("lootr.message.cannot_break").setStyle(new Style().setColor(TextFormatting.AQUA)));
          }
        } else {
          if (!player.isSneaking()) {
            event.setCanceled(true);
            player.sendMessage(new TextComponentTranslation("lootr.message.should_sneak").setStyle(new Style().setColor(TextFormatting.AQUA)));
            player.sendMessage(new TextComponentTranslation("lootr.message.should_sneak2", new TextComponentTranslation("lootr.message.should_sneak3").setStyle(new Style().setColor(TextFormatting.AQUA).setBold(true))).setStyle(new Style().setColor(TextFormatting.AQUA)));
          }
        }
      }
    }
  }
}
