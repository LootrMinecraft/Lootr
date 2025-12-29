package noobanidus.mods.lootr.common.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;

public class LootrItemFrameRenderer extends ItemFrameRenderer<LootrItemFrame> {
  public static final ModelResourceLocation FRAME_LOCATION = new ModelResourceLocation(LootrConstants.ITEM_FRAME.withPrefix("block/"), "standalone");
  public static final ModelResourceLocation FRAME_OPEN_LOCATION = new ModelResourceLocation(LootrConstants.ITEM_FRAME.withPrefix("block/")
      .withSuffix("_open"), "standalone");

  private final ItemRenderer itemRenderer;
  private final BlockRenderDispatcher blockRenderer;

  public LootrItemFrameRenderer(EntityRendererProvider.Context context) {
    super(context);
    this.itemRenderer = context.getItemRenderer();
    this.blockRenderer = context.getBlockRenderDispatcher();
  }

  public void render(LootrItemFrame entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    if (this.shouldShowName(entity)) {
      this.renderNameTag(entity, entity.getDisplayName(), poseStack, buffer, packedLight, partialTicks);
    }

    poseStack.pushPose();
    Direction direction = entity.getDirection();
    Vec3 vec3 = this.getRenderOffset(entity, partialTicks);
    poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
    poseStack.translate((double) direction.getStepX() * 0.46875, (double) direction.getStepY() * 0.46875, (double) direction.getStepZ() * 0.46875);
    poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));
    boolean flag = entity.isInvisible();
    ItemStack itemstack = entity.getItem();
    if (!flag) {
      ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
      ModelResourceLocation modelresourcelocation = entity.isClientOpened() ? FRAME_OPEN_LOCATION : FRAME_LOCATION;
      poseStack.pushPose();
      poseStack.translate(-0.5F, -0.5F, -0.5F);
      this.blockRenderer
          .getModelRenderer()
          .renderModel(
              poseStack.last(),
              buffer.getBuffer(Sheets.solidBlockSheet()),
              null,
              modelmanager.getModel(modelresourcelocation),
              1.0F,
              1.0F,
              1.0F,
              packedLight,
              OverlayTexture.NO_OVERLAY
          );
      poseStack.popPose();
    }

    if (!entity.isClientOpened()) {
      if (flag) {
        poseStack.translate(0.0F, 0.0F, 0.5F);
      } else {
        poseStack.translate(0.0F, 0.0F, 0.4375F);
      }

      int j = entity.getRotation();
      poseStack.mulPose(Axis.ZP.rotationDegrees((float) j * 360.0F / 8.0F));
      poseStack.scale(0.5F, 0.5F, 0.5F);
      this.itemRenderer
          .renderStatic(itemstack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
    }

    poseStack.popPose();
  }
}
