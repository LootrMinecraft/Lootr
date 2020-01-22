package noobanidus.mods.lootr.events;

import net.minecraft.block.Blocks;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.world.BlockEvent;
import noobanidus.mods.lootr.data.BooleanData;

public class HandleBreak {
  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    if (event.getState().getBlock() == Blocks.CHEST) {
      if (BooleanData.isLootChest(event.getWorld(), event.getPos())) {
        if (!event.getPlayer().isSneaking()) {
          event.setCanceled(true);
          event.getPlayer().sendMessage(new TranslationTextComponent("lootr.message.should_sneak").setStyle(new Style().setColor(TextFormatting.AQUA)));
        }
      }
    }
  }
}
