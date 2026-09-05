package noobanidus.mods.lootr.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.PlayerContext;
import noobanidus.mods.lootr.common.api.client.FrustumExtension;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinBlock;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinLevelRenderer;
import noobanidus.mods.lootr.common.particle.ParticleColorOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientHooks {
  @NotNull
  public static PlayerContext getPlayerContext () {
    return new PlayerContext(getPlayer());
  }

  @Nullable
  @Deprecated
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

  public static void performPotBreakEffect(int entityId, BlockPos pos) {
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

  private static double bounded(RandomSource random, double[] bounds) {
    double min = bounds[0];
    double max = bounds[1];
    return min + random.nextDouble() * (max - min);
  }

  @Nullable
  private static Frustum getFrustum () {
    Minecraft mc= Minecraft.getInstance();
    Frustum frustum1 = ((AccessorMixinLevelRenderer)mc.levelRenderer).lootr$getCapturedFrustum();
    if (frustum1 != null) {
      return frustum1;
    }
    return ((AccessorMixinLevelRenderer)mc.levelRenderer).lootr$getCullingFrustum();
  }

  public static boolean testFrustumContainsPoint (Vec3 position) {
    Frustum frustum = getFrustum();
    if (frustum == null) {
      return false;
    }

    return ((FrustumExtension)frustum).lootr$isVisible(position);
  }

  private static boolean hasLineOfSightOfBlock (ILootrInfoProvider provider) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.player == null || mc.level == null) {
      return false;
    }

    Player player = mc.player;

    Vec3 vec31 = provider.getInfoVec();
    if (!testFrustumContainsPoint(vec31)) {
      return false;
    }

    Vec3 vec3 = player.getEyePosition();

    if (vec31.distanceTo(vec3) > 128) {
      return false;
    }

    var clipResult = mc.level.clip(new ClipContext(vec3, vec31, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));

    return clipResult.getType() != HitResult.Type.MISS;
  }

  public static void performUnopenedParticles(ILootrInfoProvider provider) {
    PlayerContext context = getPlayerContext();
    if (context.hasPlayer()) {
      Level level = Minecraft.getInstance().level;
      if (level != null && !provider.hasClientOpened(context)) {
        RandomSource random = Minecraft.getInstance().level.getRandom();
        if (random.nextInt(3) == 0) {
          if (hasLineOfSightOfBlock(provider)) {
            double xOff = bounded(random, provider.getParticleXBounds());
            double zOff = bounded(random, provider.getParticleZBounds());
            Vec3 pos = provider.getParticleCenter();
            int color = provider.getParticleColor(context);
            level.addParticle(
                new ParticleColorOption(LootrRegistry.getUnopenedParticleType(), color, color, false),
                pos.x + xOff,
                pos.y + provider.getParticleYOffset() + random.nextDouble() * 0.02,
                pos.z + zOff,
                0,
                random.nextDouble() * 0.02,
                0
            );
          }
        }
      }
    }
  }
}
