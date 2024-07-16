package noobanidus.mods.lootr.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.block.entity.LootrShulkerBlockEntity;

@SuppressWarnings({"deprecation", "ConstantConditions", "NullableProblems"})
public class LootrShulkerBlockRenderer implements BlockEntityRenderer<LootrShulkerBlockEntity> {
  public static final Material MATERIAL = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("shulker"));
  public static final Material MATERIAL2 = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("shulker_opened"));
  public static final Material MATERIAL3 = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("old_shulker"));
  public static final Material MATERIAL4 = new Material(Sheets.SHULKER_SHEET, LootrAPI.rl("old_shulker_opened"));
  private final ShulkerModel<?> model;

  public LootrShulkerBlockRenderer(BlockEntityRendererProvider.Context context) {
    this.model = new ShulkerModel<>(context.bakeLayer(ModelLayers.SHULKER));
  }

  protected Material getMaterial(LootrShulkerBlockEntity blockEntity) {
    if (LootrAPI.isVanillaTextures()) {
      return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
    }
    if (blockEntity.isClientOpened()) {
      return LootrAPI.isOldTextures() ? MATERIAL3 : MATERIAL;
    } else {
      return LootrAPI.isOldTextures() ? MATERIAL4 : MATERIAL2;
    }
  }

  @Override
  public void render(LootrShulkerBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
    Direction direction = Direction.UP;
    if (pBlockEntity.hasLevel()) {
      BlockState blockstate = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos());
      if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
        direction = blockstate.getValue(ShulkerBoxBlock.FACING);
      }
    }

    Material material = getMaterial(pBlockEntity);

    pMatrixStack.pushPose();
    pMatrixStack.translate(0.5D, 0.5D, 0.5D);
    pMatrixStack.scale(0.9995F, 0.9995F, 0.9995F);
    pMatrixStack.mulPose(direction.getRotation());
    pMatrixStack.scale(1.0F, -1.0F, -1.0F);
    pMatrixStack.translate(0.0D, -1.0D, 0.0D);
    ModelPart modelpart = this.model.getLid();
    modelpart.setPos(0.0F, 24.0F - pBlockEntity.getProgress(pPartialTicks) * 0.5F * 16.0F, 0.0F);
    modelpart.yRot = 270.0F * pBlockEntity.getProgress(pPartialTicks) * ((float) Math.PI / 180F);
    VertexConsumer vertexconsumer = material.buffer(pBuffer, RenderType::entityCutoutNoCull);
    this.model.renderToBuffer(pMatrixStack, vertexconsumer, pCombinedLight, pCombinedOverlay);
    pMatrixStack.popPose();
  }
}
