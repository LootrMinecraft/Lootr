package noobanidus.mods.lootr.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.util.forge.PlatformUtilsImpl;
import noobanidus.mods.lootr.util.functions.PentaFunction;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID)
public class PlayerHandlers {
    @SubscribeEvent
    public void onPlayerBreakBlock(PlayerInteractEvent.LeftClickBlock event) {
        for (PentaFunction<Level, Player, BlockPos, BlockState, BlockEntity, Boolean> handler : PlatformUtilsImpl.PLAYER_BREAK_BLOCK_HANDLERS) {
            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            if (!handler.consume(level, event.getEntity(), pos, level.getBlockState(pos), level.getBlockEntity(pos))) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onPlayerBreakBlockCanceled(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.isCanceled()) return;
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        PlatformUtilsImpl.PLAYER_BREAK_BLOCK_CANCELED_HANDLERS.forEach(handler -> handler.consume(
                level, event.getEntity(), pos, level.getBlockState(pos), level.getBlockEntity(pos)
        ));
    }
}
