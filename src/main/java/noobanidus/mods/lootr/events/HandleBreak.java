package noobanidus.mods.lootr.events;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.Block;
import net.minecraft.Util;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.event.world.BlockEvent;
import noobanidus.mods.lootr.init.ModBlocks;

import java.util.Set;

public class HandleBreak {
  public static Set<Block> specialLootChests = Sets.newHashSet(ModBlocks.CHEST, ModBlocks.BARREL, ModBlocks.TRAPPED_CHEST, ModBlocks.SHULKER, ModBlocks.INVENTORY);

  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    if (!event.getWorld().isClientSide()) {
      if (specialLootChests.contains(event.getState().getBlock())) {
        if (!event.getPlayer().isShiftKeyDown()) {
          event.setCanceled(true);
          event.getPlayer().sendMessage(new TranslatableComponent("lootr.message.should_sneak").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), Util.NIL_UUID);
          event.getPlayer().sendMessage(new TranslatableComponent("lootr.message.should_sneak2", new TranslatableComponent("lootr.message.should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), Util.NIL_UUID);
        }
      }
    }
  }
}
