package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.block.entity.LootrBrushableBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.common.client.state.LootrBrushableBlockRenderState;
import org.jspecify.annotations.Nullable;

public class LootrBrushableBlockRenderer implements BlockEntityRenderer<LootrBrushableBlockEntity, LootrBrushableBlockRenderState> {
  private final ItemModelResolver itemModelResolver;

  public LootrBrushableBlockRenderer(BlockEntityRendererProvider.Context arg) {
    this.itemModelResolver = arg.itemModelResolver();
  }

  @Override
  public LootrBrushableBlockRenderState createRenderState() {
    return new LootrBrushableBlockRenderState();
  }

  @Override
  public void extractRenderState(LootrBrushableBlockEntity blockEntity, LootrBrushableBlockRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
    renderState.hitDirection = blockEntity.getHitDirection();
    renderState.dustProgress = blockEntity.getBlockState().getValue(BlockStateProperties.DUSTED);
    if (blockEntity.getLevel() != null && blockEntity.getHitDirection() != null) {
      renderState.lightCoords = LevelRenderer.getLightColor(
          LevelRenderer.BrightnessGetter.DEFAULT,
          blockEntity.getLevel(),
          blockEntity.getBlockState(),
          blockEntity.getBlockPos().relative(blockEntity.getHitDirection())
      );
    }

    this.itemModelResolver.updateForTopItem(renderState.itemState, blockEntity.getItem(), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
    Player player = ClientHooks.getPlayer();
    renderState.thisPlayerBrushing = player != null && blockEntity.isBrushingPlayer(player);
    renderState.visuallyOpen = player != null && blockEntity.hasClientOpened(player);
  }

  @Override
  public void submit(LootrBrushableBlockRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
    if (renderState.dustProgress > 0 && renderState.hitDirection != null && !renderState.itemState.isEmpty() && renderState.thisPlayerBrushing) {
      poseStack.pushPose();
      poseStack.translate(0.0F, 0.5F, 0.0F);
      float[] afloat = this.translations(renderState.hitDirection, renderState.dustProgress);
      poseStack.translate(afloat[0], afloat[1], afloat[2]);
      poseStack.mulPose(Axis.YP.rotationDegrees(75.0F));
      boolean flag = renderState.hitDirection == Direction.EAST || renderState.hitDirection == Direction.WEST;
      poseStack.mulPose(Axis.YP.rotationDegrees((flag ? 90 : 0) + 11));
      poseStack.scale(0.5F, 0.5F, 0.5F);
      renderState.itemState.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
      poseStack.popPose();
    }
  }

  private float[] translations(Direction arg, int i) {
    float[] afloat = new float[]{0.5F, 0.0F, 0.5F};
    float f = (float) i / 10.0F * 0.75F;
    switch (arg) {
      case EAST:
        afloat[0] = 0.73F + f;
        break;
      case WEST:
        afloat[0] = 0.25F - f;
        break;
      case UP:
        afloat[1] = 0.25F + f;
        break;
      case DOWN:
        afloat[1] = -0.23F - f;
        break;
      case NORTH:
        afloat[2] = 0.25F - f;
        break;
      case SOUTH:
        afloat[2] = 0.73F + f;
    }

    return afloat;
  }
}