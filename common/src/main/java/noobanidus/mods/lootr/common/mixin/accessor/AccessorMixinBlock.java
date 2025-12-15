package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface AccessorMixinBlock {
  @Invoker("spawnDestroyParticles")
  void lootr$spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state);
}
