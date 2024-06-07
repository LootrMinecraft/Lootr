package noobanidus.mods.lootr.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.client.item.LootrChestItemRenderer;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

public class LootrChestCartRenderer<T extends LootrChestMinecartEntity> extends MinecartRenderer<T> {
  public LootrChestCartRenderer(EntityRendererProvider.Context p_174300_, ModelLayerLocation p_174301_) {
    super(p_174300_, p_174301_);
  }

  @Override
  public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
    super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    pMatrixStack.pushPose();
    long i = (long) pEntity.getId() * 493286711L;
    i = i * i * 4392167121L + i * 98761L;
    float f = (((float) (i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
    float f1 = (((float) (i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
    float f2 = (((float) (i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
    pMatrixStack.translate(f, f1, f2);
    double d0 = Mth.lerp(pPartialTicks, pEntity.xOld, pEntity.getX());
    double d1 = Mth.lerp(pPartialTicks, pEntity.yOld, pEntity.getY());
    double d2 = Mth.lerp(pPartialTicks, pEntity.zOld, pEntity.getZ());
    Vec3 vec3 = pEntity.getPos(d0, d1, d2);
    float f3 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
    if (vec3 != null) {
      Vec3 vec31 = pEntity.getPosOffs(d0, d1, d2, 0.3F);
      Vec3 vec32 = pEntity.getPosOffs(d0, d1, d2, -0.3F);
      if (vec31 == null) {
        vec31 = vec3;
      }

      if (vec32 == null) {
        vec32 = vec3;
      }

      pMatrixStack.translate(vec3.x - d0, (vec31.y + vec32.y) / 2.0D - d1, vec3.z - d2);
      Vec3 vec33 = vec32.add(-vec31.x, -vec31.y, -vec31.z);
      if (vec33.length() != 0.0D) {
        vec33 = vec33.normalize();
        pEntityYaw = (float) (Math.atan2(vec33.z, vec33.x) * 180.0D / Math.PI);
        f3 = (float) (Math.atan(vec33.y) * 73.0D);
      }
    }

    pMatrixStack.translate(0.0D, 0.375D, 0.0D);
    pMatrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - pEntityYaw));
    pMatrixStack.mulPose(Axis.ZP.rotationDegrees(-f3));
    float f5 = (float) pEntity.getHurtTime() - pPartialTicks;
    float f6 = pEntity.getDamage() - pPartialTicks;
    if (f6 < 0.0F) {
      f6 = 0.0F;
    }

    if (f5 > 0.0F) {
      pMatrixStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f5) * f5 * f6 / 10.0F * (float) pEntity.getHurtDir()));
    }

    int j = pEntity.getDisplayOffset();
    pMatrixStack.pushPose();
    pMatrixStack.scale(0.75F, 0.75F, 0.75F);
    pMatrixStack.translate(-0.5D, (float) (j - 8) / 16.0F, 0.5D);
    pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
    LootrChestItemRenderer.getInstance().renderByMinecart(pEntity, pMatrixStack, pBuffer, pPackedLight);
    pMatrixStack.popPose();

    pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
    this.model.setupAnim(pEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
    VertexConsumer vertexconsumer = pBuffer.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
    this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    pMatrixStack.popPose();
  }
}
