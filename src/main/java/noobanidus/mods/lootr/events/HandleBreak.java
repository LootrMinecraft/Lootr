package noobanidus.mods.lootr.events;

import net.minecraft.block.Blocks;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.world.BlockEvent;
import noobanidus.mods.lootr.data.BooleanData;

public class HandleBreak {
  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    if (!event.getWorld().isRemote()) {
      if (event.getState().getBlock() == Blocks.CHEST) {
        if (BooleanData.isLootChest(event.getWorld(), event.getPos())) {
          if (!event.getPlayer().isSneaking()) {
            event.setCanceled(true);
            event.getPlayer().sendMessage(new TranslationTextComponent("lootr.message.should_sneak").setStyle(new Style().setColor(TextFormatting.AQUA)));
            event.getPlayer().sendMessage(new TranslationTextComponent("lootr.message.should_sneak2", new TranslationTextComponent("lootr.message.should_sneak3").setStyle(new Style().setBold(true))).setStyle(new Style().setColor(TextFormatting.AQUA)));
          } else {
            BooleanData.deleteLootChest(event.getWorld(), event.getPos());
          }
        }
      }
    }
  }
}
