package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.common.client.state.LootrChestBlockRenderState;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NullableProblems")
public class LootrChestBlockRenderer<T extends LootrChestBlockEntity & ILootrBlockEntity> implements BlockEntityRenderer<T, LootrChestBlockRenderState> {
  public static final Material MATERIAL = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest"));
  public static final Material MATERIAL2 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_opened"));
  public static final Material MATERIAL3 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_trapped"));
  public static final Material MATERIAL4 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("chest_trapped_opened"));
  public static final Material OLD_MATERIAL = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest"));
  public static final Material OLD_MATERIAL2 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest_opened"));
  public static final Material OLD_MATERIAL3 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest_trapped"));
  public static final Material OLD_MATERIAL4 = new Material(Sheets.CHEST_SHEET, LootrAPI.rl("old_chest_trapped_opened"));

  private final ChestModel singleModel;
  private final MaterialSet materials;

  public LootrChestBlockRenderer(BlockEntityRendererProvider.Context context) {
    this.materials = context.materials();
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

  protected Material getMaterial(LootrChestBlockRenderState state) {
    return getMaterial(state.trapped, state.visuallyOpen);
  }

  @Override
  public LootrChestBlockRenderState createRenderState() {
    return new LootrChestBlockRenderState();
  }

  @Override
  public void extractRenderState(T blockEntity, LootrChestBlockRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
    BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
    boolean flag = blockEntity.getLevel() != null;
    BlockState blockstate = flag ? blockEntity.getBlockState() : Blocks.CHEST.defaultBlockState()
        .setValue(ChestBlock.FACING, Direction.SOUTH);
    renderState.type = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
    renderState.angle = blockstate.getValue(ChestBlock.FACING).toYRot();
    renderState.open = blockEntity.getOpenNess(partialTick);
    renderState.trapped = blockEntity.getBlockState().is(LootrTags.Blocks.TRAPPED_CHESTS);
    renderState.vanilla = LootrAPI.isVanillaTextures();
    renderState.classic = LootrAPI.isOldTextures();
    renderState.visuallyOpen = Minecraft.getInstance().player != null && blockEntity.hasClientOpened(Minecraft.getInstance().player.getUUID());
    renderState.angle = blockstate.getValue(ChestBlock.FACING).toYRot();
  }

  @Override
  public void submit(LootrChestBlockRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
    poseStack.pushPose();
    poseStack.translate(0.5F, 0.5F, 0.5F);
    poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.angle));
    poseStack.translate(-0.5F, -0.5F, -0.5F);
    float f = renderState.open;
    f = 1.0F - f;
    f = 1.0F - f * f * f;
    Material material = getMaterial(renderState);
    RenderType rendertype = material.renderType(this.singleModel::renderType);
    TextureAtlasSprite textureatlassprite = this.materials.get(material);
    nodeCollector.submitModel(
        this.singleModel,
        f,
        poseStack,
        rendertype,
        renderState.lightCoords,
        OverlayTexture.NO_OVERLAY,
        -1,
        textureatlassprite,
        0,
        renderState.breakProgress
    );
    poseStack.popPose();
  }
}
