package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.integration.decorated.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.common.client.state.LootrDecoratedPotBlockRenderState;
import noobanidus.mods.lootr.common.integration.sherdsapi.SherdsIntegration;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LootrDecoratedPotRenderer implements BlockEntityRenderer<LootrDecoratedPotBlockEntity, LootrDecoratedPotBlockRenderState> {
  public static final Identifier DECORATED_POT_SHEET = Identifier.withDefaultNamespace("textures/atlas/decorated_pot.png");
  private static final SpriteId DECORATED_POT = new SpriteId(DECORATED_POT_SHEET, LootrAPI.rl("entity/pot/normal"));
  private static final SpriteId DECORATED_POT_OPENED = new SpriteId(DECORATED_POT_SHEET, LootrAPI.rl("entity/pot/normal_opened"));

  public static final ModelLayerLocation OPEN_POT_LAYER = new ModelLayerLocation(LootrConstants.DECORATED_POT, "main");

  private final SpriteGetter materials;

  private final ModelPart neck;
  private final ModelPart frontSide;
  private final ModelPart backSide;
  private final ModelPart leftSide;
  private final ModelPart rightSide;
  private final ModelPart top;
  private final ModelPart bottom;

  private final ModelPart open;
  private final ModelPart sherds;

  public LootrDecoratedPotRenderer(BlockEntityRendererProvider.Context context) {
    this(context.entityModelSet(), context.sprites());
  }

  public LootrDecoratedPotRenderer(SpecialModelRenderer.BakingContext context) {
    this(context.entityModelSet(), context.sprites());
  }

  public LootrDecoratedPotRenderer(EntityModelSet context, SpriteGetter materials) {
    this.materials = materials;
    ModelPart modelPart = context.bakeLayer(ModelLayers.DECORATED_POT_BASE);
    this.neck = modelPart.getChild("neck");
    this.top = modelPart.getChild("top");
    this.bottom = modelPart.getChild("bottom");
    ModelPart modelPart2 = context.bakeLayer(ModelLayers.DECORATED_POT_SIDES);
    this.frontSide = modelPart2.getChild("front");
    this.backSide = modelPart2.getChild("back");
    this.leftSide = modelPart2.getChild("left");
    this.rightSide = modelPart2.getChild("right");
    ModelPart modelPart3 = context.bakeLayer(OPEN_POT_LAYER);
    this.open = modelPart3.getChild("open");
    this.sherds = modelPart3.getChild("sherds");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();

    PartDefinition sherds = partdefinition.addOrReplaceChild("sherds", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

    sherds.addOrReplaceChild("angled_sherd1_r1", CubeListBuilder.create().texOffs(17, 21)
        .addBox(-0.5F, -0.5F, -4.5F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -3.5F, 1.5F, 0.0F, 0.0F, 0.829F));

    sherds.addOrReplaceChild("sherd2_r1", CubeListBuilder.create().texOffs(14, 19)
        .addBox(-4.5F, -0.5F, -4.5F, 9.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.5F, 0.5F, 0.0F, 0.1309F, 0.0F));

    sherds.addOrReplaceChild("sherd1_r1", CubeListBuilder.create().texOffs(4, 16)
        .addBox(-4.5F, -0.5F, -3.5F, 10.0F, 1.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -0.5F, -1.5F, 0.0F, -0.3054F, 0.0F));

    partdefinition.addOrReplaceChild("open", CubeListBuilder.create().texOffs(0, 0)
        .addBox(-4.0F, -0.5F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(0, 5)
        .addBox(-2.8257F, 2.4924F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, -5F, 0.0F, 0.0F, -0.1309F, -0.0873F));

    return LayerDefinition.create(meshdefinition, 64, 32);
  }

  private static final Map<Identifier, SpriteId> cachedSpriteIds = new HashMap<>();

  private static SpriteId getSideSpriteId(ItemStack item) {
    if (!item.isEmpty()) {
      Identifier customSide = SherdsIntegration.getCustomSideTexture(item);
      if (customSide != null) {
        return cachedSpriteIds.computeIfAbsent(customSide, rl -> new SpriteId(DECORATED_POT_SHEET, rl.withPrefix("entity/decorated_pot/")));
      } else {
        SpriteId material = Sheets.getDecoratedPotSprite(DecoratedPotPatterns.getPatternFromItem(item.getItem()));
        if (material != null) {
          return material;
        }
      }
    }

    return Sheets.DECORATED_POT_SIDE;
  }

  @Override
  public LootrDecoratedPotBlockRenderState createRenderState() {
    return new LootrDecoratedPotBlockRenderState();
  }

  @Override
  public void extractRenderState(LootrDecoratedPotBlockEntity blockEntity, LootrDecoratedPotBlockRenderState renderState, float partialTick, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
    renderState.decorations = null;
    renderState.potDecorations = blockEntity.getDecorations();
    renderState.direction = blockEntity.getDirection();
    DecoratedPotBlockEntity.WobbleStyle decoratedpotblockentity$wobblestyle = blockEntity.lastWobbleStyle;
    if (decoratedpotblockentity$wobblestyle != null && blockEntity.getLevel() != null) {
      renderState.wobbleProgress = ((float) (blockEntity.getLevel()
          .getGameTime() - blockEntity.wobbleStartedAtTick) + partialTick)
          / decoratedpotblockentity$wobblestyle.duration;
    } else {
      renderState.wobbleProgress = 0.0F;
    }
    Player player = ClientHooks.getPlayer();
    renderState.visuallyOpen = player != null && blockEntity.hasClientOpened(player);
  }

  @Override
  public void submit(LootrDecoratedPotBlockRenderState renderState, PoseStack poseStack, @NonNull SubmitNodeCollector nodeCollector, @NonNull CameraRenderState cameraRenderState) {
    poseStack.pushPose();
    Direction direction = renderState.direction;
    poseStack.translate(0.5, 0.0, 0.5);
    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - direction.toYRot()));
    if (!renderState.visuallyOpen) {
      poseStack.translate(-0.5, 0.0, -0.5);
      if (renderState.wobbleProgress >= 0.0F && renderState.wobbleProgress <= 1.0F) {
        if (renderState.wobbleStyle == DecoratedPotBlockEntity.WobbleStyle.POSITIVE) {
          float f1 = renderState.wobbleProgress * (float) (Math.PI * 2);
          float f2 = -1.5F * (Mth.cos(f1) + 0.5F) * Mth.sin(f1 / 2.0F);
          poseStack.rotateAround(Axis.XP.rotation(f2 * 0.015625F), 0.5F, 0.0F, 0.5F);
          float f3 = Mth.sin(f1);
          poseStack.rotateAround(Axis.ZP.rotation(f3 * 0.015625F), 0.5F, 0.0F, 0.5F);
        } else {
          float f4 = Mth.sin(-renderState.wobbleProgress * 3.0F * (float) Math.PI) * 0.125F;
          float f5 = 1.0F - renderState.wobbleProgress;
          poseStack.rotateAround(Axis.YP.rotation(f4 * f5), 0.5F, 0.0F, 0.5F);
        }
      }
    }

    this.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, renderState.potDecorations, renderState.visuallyOpen, 0);
    poseStack.popPose();
  }

  public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, PotDecorationsAdapter decorations, boolean visuallyOpen, int outlineColor) {
    TextureAtlasSprite textureatlassprite = this.materials.get(DECORATED_POT);
    TextureAtlasSprite textureatlassprite2 = this.materials.get(DECORATED_POT_OPENED);
    if (decorations == null) {
      decorations = PotDecorationsAdapter.EMPTY;
    }
    if (visuallyOpen) {
      poseStack.scale(1.0f, -1.0f, -1.0f);
      RenderType renderType = DECORATED_POT_OPENED.renderType(RenderTypes::entitySolid);
      nodeCollector.submitModelPart(this.open, poseStack, renderType, packedLight, packedOverlay, textureatlassprite2, false, false, -1, null, outlineColor);
      nodeCollector.submitModelPart(this.sherds, poseStack, renderType, packedLight, packedOverlay, textureatlassprite2, false, false, -1, null, outlineColor);
    } else {
      RenderType rendertype = DECORATED_POT.renderType(RenderTypes::entitySolid);
      nodeCollector.submitModelPart(this.neck, poseStack, rendertype, packedLight, packedOverlay, textureatlassprite, false, false, -1, null, outlineColor);
      nodeCollector.submitModelPart(this.top, poseStack, rendertype, packedLight, packedOverlay, textureatlassprite, false, false, -1, null, outlineColor);
      nodeCollector.submitModelPart(this.bottom, poseStack, rendertype, packedLight, packedOverlay, textureatlassprite, false, false, -1, null, outlineColor);
      SpriteId material = getSideSpriteId(decorations.front());
      nodeCollector.submitModelPart(
          this.frontSide,
          poseStack,
          material.renderType(RenderTypes::entitySolid),
          packedLight,
          packedOverlay,
          this.materials.get(material),
          false,
          false,
          -1,
          null,
          outlineColor
      );
      SpriteId material1 = getSideSpriteId(decorations.back());
      nodeCollector.submitModelPart(
          this.backSide,
          poseStack,
          material1.renderType(RenderTypes::entitySolid),
          packedLight,
          packedOverlay,
          this.materials.get(material1),
          false,
          false,
          -1,
          null,
          outlineColor
      );
      SpriteId material2 = getSideSpriteId(decorations.left());
      nodeCollector.submitModelPart(
          this.leftSide,
          poseStack,
          material2.renderType(RenderTypes::entitySolid),
          packedLight,
          packedOverlay,
          this.materials.get(material2),
          false,
          false,
          -1,
          null,
          outlineColor
      );
      SpriteId material3 = getSideSpriteId(decorations.right());
      nodeCollector.submitModelPart(
          this.rightSide,
          poseStack,
          material3.renderType(RenderTypes::entitySolid),
          packedLight,
          packedOverlay,
          this.materials.get(material3),
          false,
          false,
          -1,
          null,
          outlineColor
      );
    }
  }

  public void getExtents(Consumer<Vector3fc> p_470536_) {
    PoseStack posestack = new PoseStack();
    this.neck.getExtentsForGui(posestack, p_470536_);
    this.top.getExtentsForGui(posestack, p_470536_);
    this.bottom.getExtentsForGui(posestack, p_470536_);
  }
}
