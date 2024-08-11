package noobanidus.mods.lootr.event;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.LootrTags;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleBreak {

  @SubscribeEvent
  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    Player player = event.getPlayer();

    if (!event.getLevel().isClientSide()) {
      if (event.getState().is(LootrTags.Blocks.CONTAINERS)) {
        if (LootrAPI.canDestroyOrBreak(player)) {
          return;
        }
        if (LootrAPI.isBreakDisabled()) {
          if (player.getAbilities().instabuild) {
            if (!player.isShiftKeyDown()) {
              event.setCanceled(true);
              player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak").setStyle(LootrAPI.getChatStyle()), false);
            }
          } else {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break").setStyle(LootrAPI.getChatStyle()), false);
          }
        } else {
          if (!event.getPlayer().isShiftKeyDown()) {
            event.setCanceled(true);
            event.getPlayer().displayClientMessage(Component.translatable("lootr.message.should_sneak").setStyle(LootrAPI.getChatStyle()), false);
            event.getPlayer().displayClientMessage(Component.translatable("lootr.message.should_sneak2", Component.translatable("lootr.message.should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(LootrAPI.getChatStyle()), false);
          }
        }
      }
    }
  }
}
