package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.common.client.state.LootrShulkerBoxRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class LootrShulkerBoxRenderer implements BlockEntityRenderer<LootrShulkerBlockEntity, LootrShulkerBoxRenderState> {

  public static final Material MATERIAL = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("shulker"));
  public static final Material MATERIAL2 = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("shulker_opened"));

  private final MaterialSet materials;
  private final ShulkerBoxModel model;

  public LootrShulkerBoxRenderer(BlockEntityRendererProvider.Context context) {
    this(context.entityModelSet(), context.materials());
  }

  public LootrShulkerBoxRenderer(SpecialModelRenderer.BakingContext context) {
    this(context.entityModelSet(), context.materials());
  }

  public LootrShulkerBoxRenderer(EntityModelSet modelSet, MaterialSet materials) {
    this.materials = materials;
    this.model = new ShulkerBoxModel(modelSet.bakeLayer(ModelLayers.SHULKER_BOX));
  }

  @Override
  public void extractRenderState(LootrShulkerBlockEntity blockEntity, LootrShulkerBoxRenderState state, float partialTicks, Vec3 position, @Nullable ModelFeatureRenderer.CrumblingOverlay overlay) {
    BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, position, overlay);
    state.progress = blockEntity.getProgress(partialTicks);
    state.direction = blockEntity.getBlockState().getValueOrElse(ShulkerBoxBlock.FACING, Direction.UP);
    state.vanilla = LootrAPI.isVanillaTextures();
    state.classic = false;
    state.visuallyOpen = Minecraft.getInstance().player != null && blockEntity.hasClientOpened(Minecraft.getInstance().player.getUUID());
  }

  protected Material getMaterial(LootrShulkerBoxRenderState state) {

    if (state.vanilla) {
      return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
    }
    if (state.visuallyOpen) {
      return MATERIAL2;
    } else {
      return MATERIAL;
    }
  }

  @Override
  public LootrShulkerBoxRenderState createRenderState() {
    return new LootrShulkerBoxRenderState();
  }

  @Override
  public void submit(LootrShulkerBoxRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
    Material material = getMaterial(state);
    this.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.direction, state.progress, state.breakProgress, material, 0);
  }

  public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, Direction direction, float progress, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, Material material, int outlineColor) {
    poseStack.pushPose();
    this.prepareModel(poseStack, direction, progress);
    nodeCollector.submitModel(this.model, progress, poseStack, material.renderType(this.model::renderType), packedLight, packedOverlay, -1, this.materials.get(material), outlineColor, crumblingOverlay);
    poseStack.popPose();
  }

  private void prepareModel(PoseStack poseStack, Direction direction, float progress) {
    poseStack.translate(0.5F, 0.5F, 0.5F);
    poseStack.scale(0.9995F, 0.9995F, 0.9995F);
    poseStack.mulPose(direction.getRotation());
    poseStack.scale(1.0F, -1.0F, -1.0F);
    poseStack.translate(0.0F, -1.0F, 0.0F);
    this.model.setupAnim(progress);
  }

  public void getExtents(Direction direction, float progress, Consumer<Vector3fc> output) {
    PoseStack posestack = new PoseStack();
    this.prepareModel(posestack, direction, progress);
    this.model.root().getExtentsForGui(posestack, output);
  }

  public static class ShulkerBoxModel extends Model<Float> {
    private final ModelPart lid;

    public ShulkerBoxModel(ModelPart root) {
      super(root, RenderTypes::entityCutoutNoCull);
      this.lid = root.getChild("lid");
    }

    public void setupAnim(Float renderState) {
      super.setupAnim(renderState);
      this.lid.setPos(0.0F, 24.0F - renderState * 0.5F * 16.0F, 0.0F);
      this.lid.yRot = 270.0F * renderState * (float) (Math.PI / 180.0);
    }
  }
}