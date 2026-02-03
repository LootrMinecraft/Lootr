package noobanidus.mods.lootr.common.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.state.LootrItemFrameRenderState;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;

public class LootrItemFrameRenderer extends EntityRenderer<LootrItemFrame, LootrItemFrameRenderState> {
  private final ItemModelResolver itemModelResolver;
  private final BlockRenderDispatcher blockRenderer;

  public LootrItemFrameRenderer(EntityRendererProvider.Context context) {
    super(context);
    this.itemModelResolver = context.getItemModelResolver();
    this.blockRenderer = context.getBlockRenderDispatcher();
  }

  @Override
  public void submit(LootrItemFrameRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
    super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
    poseStack.pushPose();
    Direction direction = renderState.direction;
    Vec3 vec3 = this.getRenderOffset(renderState);
    poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
    poseStack.translate(direction.getStepX() * 0.46875, direction.getStepY() * 0.46875, direction.getStepZ() * 0.46875);
    float f;
    float f1;
    if (direction.getAxis().isHorizontal()) {
      f = 0.0F;
      f1 = 180.0F - direction.toYRot();
    } else {
      f = -90 * direction.getAxisDirection().getStep();
      f1 = 180.0F;
    }

    poseStack.mulPose(Axis.XP.rotationDegrees(f));
    poseStack.mulPose(Axis.YP.rotationDegrees(f1));
    if (!renderState.isInvisible) {
      BlockState blockstate = LootrBlockStateDefinitions.getItemFrameFakeState(renderState);
      BlockStateModel blockstatemodel = this.blockRenderer.getBlockModel(blockstate);
      poseStack.pushPose();
      poseStack.translate(-0.5F, -0.5F, -0.5F);
      //noinspection deprecation
      nodeCollector.submitBlockModel(
          poseStack,
          RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
          blockstatemodel,
          1.0F,
          1.0F,
          1.0F,
          renderState.lightCoords,
          OverlayTexture.NO_OVERLAY,
          renderState.outlineColor
      );
      poseStack.popPose();
    }

    if (renderState.isInvisible) {
      poseStack.translate(0.0F, 0.0F, 0.5F);
    } else {
      poseStack.translate(0.0F, 0.0F, 0.4375F);
    }

    if (!renderState.item.isEmpty()) {
      poseStack.mulPose(Axis.ZP.rotationDegrees(renderState.rotation * 360.0F / 8.0F));
      int k = renderState.lightCoords;
      poseStack.scale(0.5F, 0.5F, 0.5F);
      renderState.item.submit(poseStack, nodeCollector, k, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
    }

    poseStack.popPose();
  }

  @Override
  public Vec3 getRenderOffset(LootrItemFrameRenderState renderState) {
    return new Vec3(renderState.direction.getStepX() * 0.3F, -0.25, renderState.direction.getStepZ() * 0.3F);
  }

  @Override
  protected boolean shouldShowName(LootrItemFrame entity, double distanceToCameraSq) {
    return Minecraft.renderNames() && this.entityRenderDispatcher.crosshairPickEntity == entity && entity.getItem()
        .getCustomName() != null;
  }

  @Override
  protected Component getNameTag(LootrItemFrame entity) {
    return entity.getItem().getHoverName();
  }

  @Override
  public LootrItemFrameRenderState createRenderState() {
    return new LootrItemFrameRenderState();
  }

  @Override
  public void extractRenderState(LootrItemFrame entity, LootrItemFrameRenderState reusedState, float partialTick) {
    super.extractRenderState(entity, reusedState, partialTick);
    reusedState.direction = entity.getDirection();
    ItemStack itemstack = entity.getItem();
    this.itemModelResolver.updateForNonLiving(reusedState.item, itemstack, ItemDisplayContext.FIXED, entity);
    reusedState.rotation = entity.getRotation();
    reusedState.isGlowFrame = entity.getType() == EntityType.GLOW_ITEM_FRAME;
    reusedState.mapId = null;
    reusedState.visuallyOpen = entity.isClientOpened();
    reusedState.vanilla = LootrAPI.isVanillaTextures();
  }
}
