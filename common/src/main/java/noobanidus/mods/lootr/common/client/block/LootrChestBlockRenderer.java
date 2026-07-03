package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrChestType;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.common.client.state.LootrChestBlockRenderState;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NullableProblems")
public class LootrChestBlockRenderer<T extends LootrChestBlockEntity & ILootrBlockEntity> implements BlockEntityRenderer<T, LootrChestBlockRenderState> {
  public static final SpriteId NORMAL = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/normal"));
  public static final SpriteId NORMAL_OPENED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/normal_opened"));
  public static final SpriteId TRAPPED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/trapped"));
  public static final SpriteId TRAPPED_OPENED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/trapped_opened"));
  public static final SpriteId COPPER = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/copper"));
  public static final SpriteId COPPER_OPENED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/copper_opened"));
  public static final SpriteId COPPER_EXPOSED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/copper_exposed"));
  public static final SpriteId COPPER_EXPOSED_OPENED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/copper_exposed_opened"));
  public static final SpriteId COPPER_OXIDIZED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/copper_oxidized"));
  public static final SpriteId COPPER_OXIDIZED_OPENED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/copper_oxidized_opened"));
  public static final SpriteId COPPER_WEATHERED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/copper_weathered"));
  public static final SpriteId COPPER_WEATHERED_OPENED = new SpriteId(Sheets.CHEST_SHEET, LootrAPI.rl("entity/chest/copper_weathered_opened"));

  private final ChestModel singleModel;
  private final SpriteGetter materials;

  public LootrChestBlockRenderer(BlockEntityRendererProvider.Context context) {
    this.materials = context.sprites();
    this.singleModel = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
  }

  public static SpriteId getMaterial(LootrChestType type, boolean isOpened) {
    if (LootrAPI.isVanillaTextures()) {
      return switch (type) {
        case NORMAL -> Sheets.CHEST_REGULAR.single();
        case COPPER -> Sheets.CHEST_COPPER.unaffected().single();
        case WEATHERED -> Sheets.CHEST_COPPER.weathered().single();
        case EXPOSED -> Sheets.CHEST_COPPER.exposed().single();
        case OXIDIZED -> Sheets.CHEST_COPPER.oxidized().single();
        case TRAPPED -> Sheets.CHEST_TRAPPED.single();
      };
    }

    if (isOpened) {
      return switch (type) {
        case NORMAL -> NORMAL_OPENED;
        case COPPER -> COPPER_OPENED;
        case WEATHERED -> COPPER_WEATHERED_OPENED;
        case EXPOSED -> COPPER_EXPOSED_OPENED;
        case OXIDIZED -> COPPER_OXIDIZED_OPENED;
        case TRAPPED -> TRAPPED_OPENED;
      };
    } else {
      return switch (type) {
        case NORMAL -> NORMAL;
        case COPPER -> COPPER;
        case WEATHERED -> COPPER_WEATHERED;
        case EXPOSED -> COPPER_EXPOSED;
        case OXIDIZED -> COPPER_OXIDIZED;
        case TRAPPED -> TRAPPED;
      };
    }
  }

  protected SpriteId getMaterial(LootrChestBlockRenderState state) {
    return getMaterial(state.chestType, state.visuallyOpen);
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
    renderState.open = blockEntity.getOpenNess(partialTick);
    renderState.chestType = LootrChestType.fromState(blockEntity.getBlockState());
    renderState.vanilla = LootrAPI.isVanillaTextures();
    renderState.classic = false;
    renderState.visuallyOpen = Minecraft.getInstance().player != null && blockEntity.hasClientOpened(Minecraft.getInstance().player.getUUID());
    renderState.facing = blockstate.getValue(ChestBlock.FACING);
  }

  @Override
  public void submit(LootrChestBlockRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
    poseStack.pushPose();
    poseStack.mulPose(ChestRenderer.modelTransformation(renderState.facing));
    float open = renderState.open;
    open = 1.0f - open;
    open = 1.0f - open * open * open;
    SpriteId material = getMaterial(renderState);
    RenderType rendertype = material.renderType(this.singleModel::renderType);
    TextureAtlasSprite textureatlassprite = this.materials.get(material);
    nodeCollector.submitModel(
        this.singleModel,
        open,
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
