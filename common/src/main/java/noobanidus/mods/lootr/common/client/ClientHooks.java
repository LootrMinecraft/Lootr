package noobanidus.mods.lootr.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinBlock;
import org.jetbrains.annotations.Nullable;

public class ClientHooks {
  @Nullable
  public static Player getPlayer() {
    Minecraft mc = Minecraft.getInstance();
    //noinspection ConstantValue
    if (mc == null) {
      return null;
    }
    return mc.player;
  }

  public static void clearCache(BlockPos position) {
    final SectionPos pos = SectionPos.of(position);
    Minecraft.getInstance().submit(() -> {
      Minecraft.getInstance().levelRenderer.setSectionDirty(pos.x(), pos.y(), pos.z());
    });
  }

  public static void refreshSection() {
    Player player = getPlayer();
    if (player != null) {
      clearCache(player.blockPosition());
    }
  }

  public static void performBreakEffect(int entityId, BlockPos pos) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.level == null || mc.player == null) {
      return;
    }
    if (!(mc.level.getEntity(entityId) instanceof Player player)) {
      return;
    }
    double offset = 1.2;
    if (player == mc.player) {
      BlockState state = mc.level.getBlockState(pos);
      ((AccessorMixinBlock) state.getBlock()).lootr$spawnDestroyParticles(mc.level, player, pos, state);
      mc.level.playSound(null, pos, SoundEvents.DECORATED_POT_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    } else {
      if (mc.level.getBlockEntity(pos) instanceof ILootrBlockEntity ibe) {
        if (ibe.hasClientOpened(mc.player)) {
          offset = 0.5;
        }
      }
      mc.level.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F);
      for (int i = 0; i < 7; i++) {
        mc.level.addParticle(
            ParticleTypes.DUST_PLUME,
            false,
            pos.getX() + 0.5,
            pos.getY() + offset,
            pos.getZ() + 0.5,
            0,
            0,
            0
        );
      }
    }
  }
}
