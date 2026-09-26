package noobanidus.mods.lootr.common.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.PlayerContext;
import noobanidus.mods.lootr.common.api.client.ContainerStatus;
import noobanidus.mods.lootr.common.api.client.FrustumExtension;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.particle.ParticleColorOption;
import noobanidus.mods.lootr.common.client.gui.components.toasts.LootrToast;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinBlock;
import org.jetbrains.annotations.Nullable;

public class ClientHooks {
  public static PlayerContext getPlayerContext() {
    return new PlayerContext(getPlayer());
  }

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

  private static Frustum getFrustum() {
    Minecraft mc = Minecraft.getInstance();
    Camera camera = mc.gameRenderer.getMainCamera();
    Frustum frustum1 = camera.getCapturedFrustum();
    if (frustum1 != null) {
      return frustum1;
    }
    return camera.getCullFrustum();
  }

  public static boolean testFrustumContainsPoint(Vec3 position) {
    Frustum frustum = getFrustum();
    return ((FrustumExtension) frustum).lootr$isVisible(position);
  }

  private static boolean hasLineOfSightOfBlock(ILootrContainerInstance provider) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.player == null || mc.level == null) {
      return false;
    }

    Player player = mc.player;

    if (!testFrustumContainsPoint(provider.getParticleCenter())) {
      return false;
    }

    Vec3 vec3 = player.getEyePosition();
    Vec3 vec31 = provider.getDataVec();

    return !(vec31.distanceTo(vec3) > 128);
  }

  public static void performUnopenedParticles(ILootrContainerInstance provider) {
    PlayerContext context = getPlayerContext();
    if (context.hasPlayer()) {
      Level level = Minecraft.getInstance().level;
      if (level != null && provider.shouldDisplayParticles(context)) {
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

  public static void performRefreshParticles(ILootrContainerInstance provider) {
    PlayerContext context = getPlayerContext();
    Level level = Minecraft.getInstance().level;
    if (context.hasPlayer() && level != null && provider.hasClientOpened(context.player()) && provider.isClientRefreshing()) {
      RandomSource random = Minecraft.getInstance().level.getRandom();
      if (random.nextInt(3) == 0) {
        if (hasLineOfSightOfBlock(provider)) {
          double xOff = bounded(random, provider.getParticleXBounds());
          double zOff = bounded(random, provider.getParticleZBounds());
          Vec3 pos = provider.getParticleCenter();
          int color = LootrAPI.DEFAULT_REFRESH_PARTICLE_COLOR;
          level.addParticle(
              new ParticleColorOption(LootrRegistry.getRefreshParticleType(), color, color, false),
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

  public static void performDecayParticles(ILootrContainerInstance provider) {
    PlayerContext context = getPlayerContext();
    Level level = Minecraft.getInstance().level;
    Vec3 vec3 = provider.getParticleCenter();
    BlockState blockstate;
    if (provider instanceof BlockEntity be) {
      blockstate = be.getBlockState();
    } else {
      blockstate = LootrRegistry.getChestBlock().defaultBlockState();
    }
    if (context.hasPlayer() && level != null && provider.isClientDecaying()) {
      RandomSource random = Minecraft.getInstance().level.getRandom();
      if (random.nextInt(3) == 0) {
        if (hasLineOfSightOfBlock(provider)) {
          double xOff = bounded(random, provider.getParticleXBounds());
          double zOff = bounded(random, provider.getParticleZBounds());
          level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), vec3.x + xOff, vec3.y + provider.getParticleYOffset(), vec3.z + zOff, 0, 0, 0);
        }
      }
    }
  }

  public static void handleContainerStatus(ContainerStatus status, ContainerStatus.Type statusType, int remaining) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.player == null) {
      return;
    }
    if (!LootrAPI.shouldNotify(remaining * 20)) {
      return;
    }
    if (LootrAPI.shouldDisplayToasts()) {
      mc.getToastManager()
          .addToast(new LootrToast(mc.font, status, ContainerStatus.getMessage(status, statusType, remaining)));
    } else {
      if (statusType == ContainerStatus.Type.COMPLETE) {
        if (status == ContainerStatus.REFRESH) {
          mc.player.sendOverlayMessage(Component.translatable("lootr.message.refreshed")
              .setStyle(LootrAPI.getRefreshStyle()));
        } else {
          mc.player.sendOverlayMessage(Component.translatable("lootr.message.decayed")
              .setStyle(LootrAPI.getDecayStyle()));
        }
      } else if (statusType == ContainerStatus.Type.ONGOING) {
        if (status == ContainerStatus.REFRESH) {
          mc.player.sendOverlayMessage(Component.translatable("lootr.message.refresh_in", remaining)
              .setStyle(LootrAPI.getRefreshStyle()));
        } else {
          mc.player.sendOverlayMessage(Component.translatable("lootr.message.decay_in", remaining)
              .setStyle(LootrAPI.getDecayStyle()));
        }
      } else {
        if (status == ContainerStatus.REFRESH) {
          mc.player.sendOverlayMessage(Component.translatable("lootr.message.refresh_start", remaining)
              .setStyle(LootrAPI.getRefreshStyle()));
        } else {
          mc.player.sendOverlayMessage(Component.translatable("lootr.message.decay_start", remaining)
              .setStyle(LootrAPI.getDecayStyle()));
        }
      }
    }
  }
}
