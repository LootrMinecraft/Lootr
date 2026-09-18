package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleBreak {

  @SubscribeEvent
  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    if (event.getLevel().isClientSide() || !(event.getState().is(LootrTags.Blocks.CONTAINERS))) {
      return;
    }

    Player player = event.getPlayer();

    BlockEntity block = event.getLevel().getBlockEntity(event.getPos());
    if (LootrAPI.resolveBlockEntity(block) instanceof ILootrBlockEntity lbe) {
      if (!lbe.hasLootTable() && !lbe.isInfoReferenceInventory()) {
        return;
      }
      if (LootrAPI.canDestroyOrBreak(player)) {
        return;
      }
      boolean dumped = false;
      if (LootrAPI.breakToDropLoot() && !player.isShiftKeyDown()) {
        LootrAPI.dumpPlayerLoot(lbe, (ServerPlayer) player, (ServerLevel) event.getLevel());
        dumped = true;
      }
      if (LootrAPI.isBreakDisabled()) {
        if (player.getAbilities().instabuild) {
          if (!player.isShiftKeyDown()) {
            event.setCanceled(true);
            if (!dumped) {
              player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak")
                  .setStyle(LootrAPI.getChatStyle()), false);
            }
          }
        } else {
          event.setCanceled(true);
          if (!dumped) {
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break")
                .setStyle(LootrAPI.getChatStyle()), false);
          }
        }
      } else {
        if (!event.getPlayer().isShiftKeyDown()) {
          event.setCanceled(true);
          if (!dumped) {
            event.getPlayer().displayClientMessage(Component.translatable("lootr.message.should_sneak")
                .setStyle(LootrAPI.getChatStyle()), false);
            event.getPlayer()
                .displayClientMessage(Component.translatable("lootr.message.should_sneak2")
                    .setStyle(LootrAPI.getChatStyle()), false);
          }
        }
      }
    }
  }
}
