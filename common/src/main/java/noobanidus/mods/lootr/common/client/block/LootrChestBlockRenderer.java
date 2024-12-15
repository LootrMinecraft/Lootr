package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class LootrChestBlockRenderer<T extends LootrChestBlockEntity & ILootrBlockEntity> extends ChestRenderer<T> {
  public static final Material MATERIAL = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest"));
  public static final Material MATERIAL2 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_opened"));
  public static final Material MATERIAL3 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_trapped"));
  public static final Material MATERIAL4 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_trapped_opened"));
  public static final Material OLD_MATERIAL = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest"));
  public static final Material OLD_MATERIAL2 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest_opened"));
  public static final Material OLD_MATERIAL3 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest_trapped"));
  public static final Material OLD_MATERIAL4 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest_trapped_opened"));

  private final ChestModel singleModel;

  public LootrChestBlockRenderer(BlockEntityRendererProvider.Context context) {
    super(context);
    this.singleModel = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
  }

  public static Material getMaterial(boolean isTrapped, boolean isOpened) {
    if (LootrAPI.isVanillaTextures()) {
      if (isTrapped) {
        return Sheets.CHEST_TRAP_LOCATION;
      } else {
        return Sheets.CHEST_LOCATION;
      }
    }
    if (LootrAPI.isOldTextures()) {
      if (isOpened) {
        return isTrapped ? OLD_MATERIAL4 : OLD_MATERIAL2;
      } else {
        return isTrapped ? OLD_MATERIAL3 : OLD_MATERIAL;
      }
    } else {
      if (isOpened) {
        return isTrapped ? MATERIAL4 : MATERIAL2;
      } else {
        return isTrapped ? MATERIAL3 : MATERIAL;
      }
    }
  }

  protected Material getMaterial(T blockEntity) {
    boolean trapped = blockEntity.getType().builtInRegistryHolder().is(LootrTags.BlockEntity.TRAPPED);
    boolean opened = Minecraft.getInstance().player != null && blockEntity.hasClientOpened(Minecraft.getInstance().player.getUUID());
    return getMaterial(trapped, opened);
  }

  @Override
  public void render(T blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
    Level level = blockEntity.getLevel();
    boolean bl = level != null;
    BlockState blockState = bl ? blockEntity.getBlockState() : LootrRegistry.getChestBlock().defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
    poseStack.pushPose();
    float g = blockState.getValue(ChestBlock.FACING).toYRot();
    poseStack.translate(0.5F, 0.5F, 0.5F);
    poseStack.mulPose(Axis.YP.rotationDegrees(-g));
    poseStack.translate(-0.5F, -0.5F, -0.5F);
    float h = 1.0f - blockEntity.getOpenNess(f);
    h = 1.0F - h * h * h;
    Material material = getMaterial(blockEntity);
    VertexConsumer vertexConsumer = material.buffer(multiBufferSource, RenderType::entityCutout);
    this.render(poseStack, vertexConsumer, this.singleModel, h, i, j);
    poseStack.popPose();
  }

  private void render(PoseStack poseStack, VertexConsumer vertexConsumer, ChestModel chestModel, float f, int i, int j) {
    chestModel.setupAnim(f);
    chestModel.renderToBuffer(poseStack, vertexConsumer, i, j);
  }
}
