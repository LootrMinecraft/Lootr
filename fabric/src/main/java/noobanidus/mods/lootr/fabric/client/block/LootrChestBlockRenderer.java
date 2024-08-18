package noobanidus.mods.lootr.fabric.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class LootrChestBlockRenderer<T extends LootrChestBlockEntity & ILootrBlockEntity> extends ChestRenderer<T> {
  public static final Material MATERIAL = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest"));
  public static final Material MATERIAL2 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_opened"));
  public static final Material MATERIAL3 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_trapped"));
  public static final Material MATERIAL4 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_trapped_opened"));
  public static final Material OLD_MATERIAL = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest"));
  public static final Material OLD_MATERIAL2 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest_opened"));
  private final ModelPart lid;
  private final ModelPart bottom;
  private final ModelPart lock;
  private UUID playerId = null;

  public LootrChestBlockRenderer(BlockEntityRendererProvider.Context context) {
    super(context);
    ModelPart modelPart = context.bakeLayer(ModelLayers.CHEST);
    this.bottom = modelPart.getChild("bottom");
    this.lid = modelPart.getChild("lid");
    this.lock = modelPart.getChild("lock");
  }

  public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    Level level = blockEntity.getLevel();
    BlockState blockState = level != null ? blockEntity.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
    poseStack.pushPose();
    float f = blockState.getValue(ChestBlock.FACING).toYRot();
    poseStack.translate(0.5D, 0.5D, 0.5D);
    poseStack.mulPose(Axis.YP.rotationDegrees(-f));
    poseStack.translate(-0.5D, -0.5D, -0.5D);

    float g = blockEntity.getOpenNess(partialTick);
    g = 1.0F - g;
    g = 1.0F - g * g * g;
    Material material = getMaterial(blockEntity);
    VertexConsumer vertexConsumer = material.buffer(bufferSource, RenderType::entityCutout);
    this.render(poseStack, vertexConsumer, this.lid, this.lock, this.bottom, g, packedLight, packedOverlay);

    poseStack.popPose();
  }

  private void render(PoseStack poseStack, VertexConsumer consumer, ModelPart lidPart, ModelPart lockPart, ModelPart bottomPart, float lidAngle, int packedLight, int packedOverlay) {
    lidPart.xRot = -(lidAngle * 1.5707964F);
    lockPart.xRot = lidPart.xRot;
    lidPart.render(poseStack, consumer, packedLight, packedOverlay);
    lockPart.render(poseStack, consumer, packedLight, packedOverlay);
    bottomPart.render(poseStack, consumer, packedLight, packedOverlay);
  }

  protected Material getMaterial(T blockEntity) {
    if (LootrAPI.isVanillaTextures()) {
      return Sheets.chooseMaterial(blockEntity, ChestType.SINGLE, false);
    }
    if (Minecraft.getInstance().player == null) {
      return MATERIAL2;
    }
    // TODO: ???
    boolean trapped = blockEntity.getType().equals(LootrRegistry.getTrappedChestBlockEntity());
    if (blockEntity.hasClientOpened(Minecraft.getInstance().player.getUUID())) {
      if (LootrAPI.isOldTextures()) {
        return OLD_MATERIAL2;
      }
      return trapped ? MATERIAL4 : MATERIAL2;
    } else {
      if (LootrAPI.isOldTextures()) {
        return OLD_MATERIAL;
      }
      return trapped ? MATERIAL3 : MATERIAL;
    }
  }
}
