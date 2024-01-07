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

public class LootrChestCartRenderer <T extends LootrChestMinecartEntity> extends MinecartRenderer<T> {
    public LootrChestCartRenderer(EntityRendererProvider.Context arg, ModelLayerLocation arg2) {
        super(arg, arg2);
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, yaw, partialTicks, matrixStack, buffer, packedLight);

        matrixStack.pushPose();
        long i = entity.getId() * 493286711l;
        i = i * i * 4392167121l + i * 98761l;
        float f = (((float) (i >> 16 & 7l) + 0.5f) / 8f - 0.5f) * 0.004f;
        float f1 = (((float) (i >> 20 & 7l) + 0.5f) / 8f - 0.5f) * 0.004f;
        float f2 = (((float) (i >> 24 & 7l) + 0.5f) / 8f - 0.5f) * 0.004f;
        matrixStack.translate(f, f1, f2);
        double d0 = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double d1 = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double d2 = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
        Vec3 vec3 = entity.getPos(d0, d1, d2);
        float f3 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        if (vec3 != null) {
            Vec3 vec31 = entity.getPosOffs(d0, d1, d2, 0.3f);
            Vec3 vec32 = entity.getPosOffs(d0, d1, d2, -0.3f);
            if (vec31 == null) {
                vec31 = vec3;
            }
            if (vec32 == null) {
                vec32 = vec3;
            }

            matrixStack.translate(vec3.x - d0, (vec31.y + vec32.y) /2d - d1, vec3.z - d2);
            Vec3 vec33 = vec32.add(-vec31.x, -vec31.y, -vec31.z);
            if (vec33.length() != 0.0D) {
                vec33 = vec33.normalize();
                yaw = (float) (Math.atan2(vec33.z, vec33.x) * 180.0D / Math.PI);
                f3 = (float) (Math.atan(vec33.y) * 73.0D);
            }
        }

        matrixStack.translate(0.0D, 0.375D, 0.0D);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-f3));
        float f5 = (float) entity.getHurtTime() - partialTicks;
        float f6 = entity.getDamage() - partialTicks;
        if (f6 < 0.0F) {
            f6 = 0.0F;
        }

        if (f5 > 0.0F) {
            matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f5) * f5 * f6 / 10.0F * (float) entity.getHurtDir()));
        }

        int j = entity.getDisplayOffset();
        matrixStack.pushPose();
        matrixStack.scale(0.75F, 0.75F, 0.75F);
        matrixStack.translate(-0.5D, (float) (j - 8) / 16.0F, 0.5D);
        matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        // TODO: Tile
        LootrChestItemRenderer.getInstance().renderByMinecart(entity, matrixStack, buffer, packedLight);
        matrixStack.popPose();

        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.model.setupAnim(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(matrixStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }
}
