package noobanidus.mods.lootr.common.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;

public class LootrChestCartRenderer<T extends LootrChestMinecartEntity> extends AbstractMinecartRenderer<T, LootrCartRenderState> {
  private final ChestModel chestModel;

  public LootrChestCartRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelLayerLocation) {
    super(context, modelLayerLocation);
    this.chestModel = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
  }

  @Override
  public void render(LootrCartRenderState pEntity, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
    super.render(pEntity, pMatrixStack, pBuffer, pPackedLight);
  }

  @Override
  protected void renderMinecartContents(LootrCartRenderState minecartRenderState, BlockState blockState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
    Material material = LootrChestBlockRenderer.getMaterial(false, minecartRenderState.open);
    VertexConsumer consumer = material.buffer(multiBufferSource, RenderType::entitySolid);
    this.chestModel.setupAnim(0f);
    this.chestModel.renderToBuffer(poseStack, consumer, i, OverlayTexture.NO_OVERLAY);
  }

  @Override
  public LootrCartRenderState createRenderState() {
    return new LootrCartRenderState();
  }

  @Override
  public void extractRenderState(T abstractMinecart, LootrCartRenderState minecartRenderState, float f) {
    super.extractRenderState(abstractMinecart, minecartRenderState, f);
    minecartRenderState.open = abstractMinecart.isClientOpened();
  }
}
