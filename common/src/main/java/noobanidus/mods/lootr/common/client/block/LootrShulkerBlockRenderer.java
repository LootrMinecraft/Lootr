package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;

import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class LootrShulkerBlockRenderer implements BlockEntityRenderer<LootrShulkerBlockEntity> {
  public static final Material MATERIAL = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("shulker"));
  public static final Material MATERIAL2 = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("shulker_opened"));
  public static final Material MATERIAL3 = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("old_shulker"));
  public static final Material MATERIAL4 = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("old_shulker_opened"));
  private final ShulkerBoxModel model;

  public LootrShulkerBlockRenderer(BlockEntityRendererProvider.Context context) {
    this.model = new ShulkerBoxModel(context.bakeLayer(ModelLayers.SHULKER));
  }

  protected Material getMaterial(LootrShulkerBlockEntity blockEntity) {
    if (LootrAPI.isVanillaTextures()) {
      return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
    }
    if (Minecraft.getInstance().player == null) {
      return MATERIAL2;
    }
    if (blockEntity.hasClientOpened(Minecraft.getInstance().player.getUUID())) {
      return LootrAPI.isOldTextures() ? MATERIAL4 : MATERIAL2;
    } else {
      return LootrAPI.isOldTextures() ? MATERIAL3 : MATERIAL;
    }
  }

  @Override
  public void render(LootrShulkerBlockEntity shulkerBoxBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
    Direction direction = Direction.UP;
    if (shulkerBoxBlockEntity.hasLevel()) {
      BlockState blockState = shulkerBoxBlockEntity.getLevel().getBlockState(shulkerBoxBlockEntity.getBlockPos());
      if (blockState.getBlock() instanceof ShulkerBoxBlock) {
        direction = (Direction)blockState.getValue(ShulkerBoxBlock.FACING);
      }
    }

    Material material = getMaterial(shulkerBoxBlockEntity);

    poseStack.pushPose();
    poseStack.translate(0.5F, 0.5F, 0.5F);
    float g = 0.9995F;
    poseStack.scale(0.9995F, 0.9995F, 0.9995F);
    poseStack.mulPose(direction.getRotation());
    poseStack.scale(1.0F, -1.0F, -1.0F);
    poseStack.translate(0.0F, -1.0F, 0.0F);
    this.model.animate(shulkerBoxBlockEntity, f);
    ShulkerBoxModel var10002 = this.model;
    Objects.requireNonNull(var10002);
    VertexConsumer vertexConsumer = material.buffer(multiBufferSource, var10002::renderType);
    this.model.renderToBuffer(poseStack, vertexConsumer, i, j);
    poseStack.popPose();
  }

  public static class ShulkerBoxModel extends Model {
    private final ModelPart lid;

    public ShulkerBoxModel(ModelPart modelPart) {
      super(modelPart, RenderType::entityCutoutNoCull);
      this.lid = modelPart.getChild("lid");
    }

    public void animate(LootrShulkerBlockEntity shulkerBoxBlockEntity, float f) {
      this.lid.setPos(0.0F, 24.0F - shulkerBoxBlockEntity.getProgress(f) * 0.5F * 16.0F, 0.0F);
      this.lid.yRot = 270.0F * shulkerBoxBlockEntity.getProgress(f) * 0.017453292F;
    }

    public void animate (float f) {
      this.lid.setPos(0.0f, 24.0f - f * 0.5f * 16.0f, 0.0f);
      this.lid.yRot = 270.0f * f * (float) (Math.PI/180);
    }
  }
}