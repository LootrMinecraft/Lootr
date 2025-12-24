package noobanidus.mods.lootr.common.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;

public class LootrChestCartRenderer<T extends LootrChestMinecartEntity> extends AbstractMinecartRenderer<T, LootrCartRenderState> {
  private final MaterialSet materials;
  private final ChestModel chestModel;

  public LootrChestCartRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelLayerLocation) {
    super(context, modelLayerLocation);
    this.chestModel = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
    this.materials = context.getMaterials();
  }

  @Override
  protected void submitMinecartContents(LootrCartRenderState renderState, BlockState blockState, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight) {
    poseStack.pushPose();
    poseStack.translate(0.5F, 0.5F, 0.5F);
    poseStack.translate(-0.5F, -0.5F, -0.5F);
    float f = 0;
    f = 1.0F - f;
    f = 1.0F - f * f * f;
    Material material = LootrChestBlockRenderer.getMaterial(false, renderState.open);
    RenderType rendertype = material.renderType(this.chestModel::renderType);
    TextureAtlasSprite textureatlassprite = this.materials.get(material);
    nodeCollector.submitModel(
        this.chestModel,
        f,
        poseStack,
        rendertype,
        renderState.lightCoords,
        OverlayTexture.NO_OVERLAY,
        -1,
        textureatlassprite,
        0,
        null
    );
    poseStack.popPose();
  }

  @Override
  public LootrCartRenderState createRenderState() {
    return new LootrCartRenderState();
  }

  @Override
  public void extractRenderState(T abstractMinecart, LootrCartRenderState minecartRenderState, float f) {
    super.extractRenderState(abstractMinecart, minecartRenderState, f);
    minecartRenderState.open = abstractMinecart.isClientOpened();
    minecartRenderState.classic = LootrAPI.isOldTextures();
    minecartRenderState.vanilla = LootrAPI.isVanillaTextures();
  }
}
