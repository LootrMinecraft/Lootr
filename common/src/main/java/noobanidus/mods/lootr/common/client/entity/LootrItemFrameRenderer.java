package noobanidus.mods.lootr.common.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BlockStateDefinitions;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.common.client.state.LootrItemFrameRenderState;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;
import org.jspecify.annotations.NonNull;

public class LootrItemFrameRenderer extends EntityRenderer<LootrItemFrame, LootrItemFrameRenderState> {
  private final ItemModelResolver itemModelResolver;
  private final BlockModelResolver blockRenderer;

  public LootrItemFrameRenderer(EntityRendererProvider.Context context) {
    super(context);
    this.itemModelResolver = context.getItemModelResolver();
    this.blockRenderer = context.getBlockModelResolver();
  }

  @Override
  public void submit(LootrItemFrameRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
    super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
    poseStack.pushPose();
    Direction direction = state.direction;
    Vec3 renderOffset = this.getRenderOffset(state);
    poseStack.translate(-renderOffset.x(), -renderOffset.y(), -renderOffset.z());
    poseStack.translate(direction.getStepX() * 0.46875, direction.getStepY() * 0.46875, direction.getStepZ() * 0.46875);
    float xRot;
    float yRot;
    if (direction.getAxis().isHorizontal()) {
      xRot = 0.0F;
      yRot = 180.0F - direction.toYRot();
    } else {
      xRot = -90 * direction.getAxisDirection().getStep();
      yRot = 180.0F;
    }

    poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
    poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
    if (!state.frameModel.isEmpty()) {
      poseStack.pushPose();
      poseStack.translate(-0.5F, -0.5F, -0.5F);
      state.frameModel.submitWithZOffset(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
      poseStack.popPose();
    }

    if (state.isInvisible) {
      poseStack.translate(0.0F, 0.0F, 0.5F);
    } else {
      poseStack.translate(0.0F, 0.0F, 0.4375F);
    }

    if (!state.item.isEmpty() && !state.visuallyOpen) {
      poseStack.mulPose(Axis.ZP.rotationDegrees(state.rotation * 360.0F / 8.0F));
      int lightVal = this.getLightCoords(state.isGlowFrame, 15728880, state.lightCoords);
      poseStack.scale(0.5F, 0.5F, 0.5F);
      state.item.submit(poseStack, submitNodeCollector, lightVal, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }

    poseStack.popPose();
  }

  @Override
  public @NonNull Vec3 getRenderOffset(LootrItemFrameRenderState renderState) {
    return new Vec3(renderState.direction.getStepX() * 0.3F, -0.25, renderState.direction.getStepZ() * 0.3F);
  }

  private int getLightCoords(boolean isGlowFrame, int glowLightCoords, int originalLightCoords) {
    return isGlowFrame ? glowLightCoords : originalLightCoords;
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
  public void extractRenderState(LootrItemFrame entity, LootrItemFrameRenderState state, float partialTick) {
    super.extractRenderState(entity, state, partialTick);
    state.direction = entity.getDirection();
    ItemStack itemStack = entity.getItem();
    this.itemModelResolver.updateForNonLiving(state.item, itemStack, ItemDisplayContext.FIXED, entity);
    state.rotation = entity.getRotation();
    state.isGlowFrame = entity.is(EntityType.GLOW_ITEM_FRAME);
    state.mapId = null;

    Player player = ClientHooks.getPlayer();
    boolean visuallyOpen = player != null && entity.hasClientOpened(player);
    boolean vanilla = LootrAPI.isVanillaTextures();

    if (!state.isInvisible) {
      // TODO: The frame state is here
      // this controls vanilla vs lootr vs lootr opened
      BlockState fakeState;
      if (vanilla) {
        fakeState = BlockStateDefinitions.getItemFrameFakeState(state.isGlowFrame, false);
      } else {
        fakeState = LootrBlockStateDefinitions.getItemFrameFakeState(visuallyOpen);
      }

      this.blockRenderer.update(state.frameModel, fakeState, ItemFrameRenderer.BLOCK_DISPLAY_CONTEXT);
    } else {
      state.frameModel.clear();
    }

    state.visuallyOpen = visuallyOpen;
    state.vanilla = vanilla;
  }
}
