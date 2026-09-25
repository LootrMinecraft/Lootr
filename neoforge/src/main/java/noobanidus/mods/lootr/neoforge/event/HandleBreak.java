package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleBreak {

  @SubscribeEvent
  public static void onBlockBreak(BreakBlockEvent event) {
    if (!event.getState().is(LootrTags.Blocks.PREVENT_BREAK) || event.getState().is(LootrTags.Blocks.ENABLE_BREAK)) {
      return;
    }

    LevelAccessor level = event.getLevel();

    Player player = event.getPlayer();

    BlockEntity block = level.getBlockEntity(event.getPos());
    if (!(LootrAPI.wrapBlockEntity(block) instanceof ILootrBlockEntity lbe)) {
      return;
    }
    if (!lbe.hasLootTable() && !lbe.isDataReferenceInventory()) {
      return;
    }
    if (LootrAPI.canDestroyOrBreak(player)) {
      return;
    }
    boolean dumped = false;
    if (!player.level().isClientSide() && LootrAPI.breakToDropLoot() && !player.isShiftKeyDown()) {
      LootrAPI.dumpPlayerLoot(lbe, (ServerPlayer) player, (ServerLevel) event.getLevel());
      dumped = true;
    }

    if (LootrAPI.isBreakDisabled()) {
      if (player.getAbilities().instabuild) {
        if (!player.isShiftKeyDown()) {
          event.setCanceled(true);
          if (!level.isClientSide()) {
            if (!dumped) {
              player.sendSystemMessage(Component.translatable("lootr.message.cannot_break_sneak")
                  .setStyle(LootrAPI.getChatStyle()));
            }
          }
        }
      } else {
        event.setCanceled(true);
        if (!level.isClientSide()) {
          event.setNotifyClient(true);
          if (!dumped) {
            player.sendSystemMessage(Component.translatable("lootr.message.cannot_break")
                .setStyle(LootrAPI.getChatStyle()));
          }
        }
      }
    } else {
      if (!event.getPlayer().isShiftKeyDown()) {
        event.setCanceled(true);
        if (!level.isClientSide()) {
          event.setNotifyClient(true);
          if (!dumped) {
            event.getPlayer().sendSystemMessage(Component.translatable("lootr.message.should_sneak")
                .setStyle(LootrAPI.getChatStyle()));
            event.getPlayer()
                .sendSystemMessage(Component.translatable("lootr.message.should_sneak2")
                    .setStyle(LootrAPI.getChatStyle()));
          }
        }
      }
    }
  }
}
