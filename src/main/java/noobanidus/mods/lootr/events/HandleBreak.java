package noobanidus.mods.lootr.events;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.util.Util;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.world.BlockEvent;
import noobanidus.mods.lootr.init.ModBlocks;

import java.util.Set;

public class HandleBreak {
  public static Set<Block> specialLootChests = Sets.newHashSet(ModBlocks.CHEST, ModBlocks.BARREL, ModBlocks.TRAPPED_CHEST, ModBlocks.SHULKER);

  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    if (!event.getWorld().isClientSide()) {
      if (specialLootChests.contains(event.getState().getBlock())) {
        if (!event.getPlayer().isShiftKeyDown()) {
          event.setCanceled(true);
          event.getPlayer().sendMessage(new TranslationTextComponent("lootr.message.should_sneak").setStyle(Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.AQUA))), Util.NIL_UUID);
          event.getPlayer().sendMessage(new TranslationTextComponent("lootr.message.should_sneak2", new TranslationTextComponent("lootr.message.should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.AQUA))), Util.NIL_UUID);
        }
      }
    }
  }
}
