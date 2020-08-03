package noobanidus.mods.lootr.events;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.world.BlockEvent;
import noobanidus.mods.lootr.init.ModBlocks;

import java.util.Set;

public class HandleBreak {
  public static Set<Block> specialLootChests = Sets.newHashSet(ModBlocks.CHEST, ModBlocks.BARREL, ModBlocks.TRAPPED_CHEST);

  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    if (!event.getWorld().isRemote()) {
      if (specialLootChests.contains(event.getState().getBlock())) {
        if (!event.getPlayer().isSneaking()) {
          event.setCanceled(true);
          event.getPlayer().sendMessage(new TranslationTextComponent("lootr.message.should_sneak").setStyle(new Style().setColor(TextFormatting.AQUA)));
          event.getPlayer().sendMessage(new TranslationTextComponent("lootr.message.should_sneak2", new TranslationTextComponent("lootr.message.should_sneak3").setStyle(new Style().setBold(true))).setStyle(new Style().setColor(TextFormatting.AQUA)));
        }
      }
    }
  }
}
