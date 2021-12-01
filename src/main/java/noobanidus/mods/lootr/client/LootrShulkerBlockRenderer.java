package noobanidus.mods.lootr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.blocks.entities.LootrShulkerBlockEntity;

import java.util.UUID;

@SuppressWarnings({"deprecation", "ConstantConditions", "NullableProblems"})
public class LootrShulkerBlockRenderer implements BlockEntityRenderer<LootrShulkerBlockEntity> {
  public static final Material MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Lootr.MODID, "shulker"));
  public static final Material MATERIAL2 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Lootr.MODID, "shulker_opened"));
  private UUID playerId;

  private final ShulkerModel<?> model;

  public LootrShulkerBlockRenderer(BlockEntityRendererProvider.Context context) {
    this.model = new ShulkerModel<>(context.bakeLayer(ModelLayers.SHULKER));
  }

  protected Material getMaterial(LootrShulkerBlockEntity tile) {
    if (playerId == null) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
        return MATERIAL;
      } else {
        playerId = mc.player.getUUID();
      }
    }
    if (tile.getOpeners().contains(playerId)) {
      return MATERIAL2;
    } else {
      return MATERIAL;
    }
  }

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
    float f = 0.9995F;
    pMatrixStack.scale(0.9995F, 0.9995F, 0.9995F);
    pMatrixStack.mulPose(direction.getRotation());
    pMatrixStack.scale(1.0F, -1.0F, -1.0F);
    pMatrixStack.translate(0.0D, -1.0D, 0.0D);
    ModelPart modelpart = this.model.getLid();
    modelpart.setPos(0.0F, 24.0F - pBlockEntity.getProgress(pPartialTicks) * 0.5F * 16.0F, 0.0F);
    modelpart.yRot = 270.0F * pBlockEntity.getProgress(pPartialTicks) * ((float)Math.PI / 180F);
    VertexConsumer vertexconsumer = material.buffer(pBuffer, RenderType::entityCutoutNoCull);
    this.model.renderToBuffer(pMatrixStack, vertexconsumer, pCombinedLight, pCombinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    pMatrixStack.popPose();
  }
}
