package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.common.integration.sherdsapi.SherdsIntegration;

import java.util.HashMap;
import java.util.Map;

public class LootrDecoratedPotRenderer implements BlockEntityRenderer<LootrDecoratedPotBlockEntity> {
  public static final ResourceLocation DECORATED_POT_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/decorated_pot.png");
  private static final Material DECORATED_POT = new Material(DECORATED_POT_SHEET, LootrAPI.rl("entity/loot_pot"));
  private static final Material DECORATED_POT_OPENED = new Material(DECORATED_POT_SHEET, LootrAPI.rl("entity/loot_pot_open"));

  public static final ModelLayerLocation OPEN_POT_LAYER = new ModelLayerLocation(LootrProperties.DECORATED_POT, "main");

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

		PartDefinition angled_sherd1_r1 = sherds.addOrReplaceChild("angled_sherd1_r1", CubeListBuilder.create().texOffs(17, 21).addBox(-0.5F, -0.5F, -4.5F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -3.5F, 1.5F, 0.0F, 0.0F, 0.829F));

		PartDefinition sherd2_r1 = sherds.addOrReplaceChild("sherd2_r1", CubeListBuilder.create().texOffs(14, 19).addBox(-4.5F, -0.5F, -4.5F, 9.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.5F, 0.5F, 0.0F, 0.1309F, 0.0F));

		PartDefinition sherd1_r1 = sherds.addOrReplaceChild("sherd1_r1", CubeListBuilder.create().texOffs(4, 16).addBox(-4.5F, -0.5F, -3.5F, 10.0F, 1.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -0.5F, -1.5F, 0.0F, -0.3054F, 0.0F));

		PartDefinition open = partdefinition.addOrReplaceChild("open", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -0.5F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 5).addBox(-2.8257F, 2.4924F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, -5F, 0.0F, 0.0F, -0.1309F, -0.0873F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

  private static final Map<ResourceLocation, Material> cachedMaterials = new HashMap<>();

  // TODO: How does this handle custom pot patterns?
  private static Material getSideMaterial(ItemStack item) {
    if (!item.isEmpty()) {
      ResourceLocation customSide = SherdsIntegration.getCustomSideTexture(item);
      if (customSide != null) {
        return cachedMaterials.computeIfAbsent(customSide, rl -> new Material(DECORATED_POT_SHEET, rl));
      } else {
        Material material = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getPatternFromItem(item.getItem()));
        if (material != null) {
          return material;
        }
      }
    }

    return Sheets.DECORATED_POT_SIDE;
  }

  public void render(LootrDecoratedPotBlockEntity decoratedPotBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
    Player player = ClientHooks.getPlayer();
    if (player == null) {
      return;
    }

    boolean opened = decoratedPotBlockEntity.hasClientOpened(player.getUUID());

    poseStack.pushPose();
    Direction direction = decoratedPotBlockEntity.getDirection();
    poseStack.translate(0.5, 0.0, 0.5);
    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - direction.toYRot()));
    if (!opened) {
      poseStack.translate(-0.5, 0.0, -0.5);
      // Don't wobble if open
      DecoratedPotBlockEntity.WobbleStyle wobbleStyle = decoratedPotBlockEntity.lastWobbleStyle;
      if (wobbleStyle != null && decoratedPotBlockEntity.getLevel() != null) {
        float g = ((float) (decoratedPotBlockEntity.getLevel()
            .getGameTime() - decoratedPotBlockEntity.wobbleStartedAtTick) + f) / (float) wobbleStyle.duration;
        if (g >= 0.0F && g <= 1.0F) {
          if (wobbleStyle == DecoratedPotBlockEntity.WobbleStyle.POSITIVE) {
            float h = 0.015625F;
            float k = g * (float) (Math.PI * 2);
            float l = -1.5F * (Mth.cos(k) + 0.5F) * Mth.sin(k / 2.0F);
            poseStack.rotateAround(Axis.XP.rotation(l * 0.015625F), 0.5F, 0.0F, 0.5F);
            float m = Mth.sin(k);
            poseStack.rotateAround(Axis.ZP.rotation(m * 0.015625F), 0.5F, 0.0F, 0.5F);
          } else {
            float h = Mth.sin(-g * 3.0F * (float) Math.PI) * 0.125F;
            float k = 1.0F - g;
            poseStack.rotateAround(Axis.YP.rotation(h * k), 0.5F, 0.0F, 0.5F);
          }
        }
      }
      VertexConsumer vertexConsumer = DECORATED_POT.buffer(multiBufferSource, RenderType::entitySolid);
      this.neck.render(poseStack, vertexConsumer, i, j);
      this.top.render(poseStack, vertexConsumer, i, j);
      this.bottom.render(poseStack, vertexConsumer, i, j);
      PotDecorationsAdapter potDecorations = decoratedPotBlockEntity.getDecorations();
      this.renderSide(this.frontSide, poseStack, multiBufferSource, i, j, getSideMaterial(potDecorations.front()));
      this.renderSide(this.backSide, poseStack, multiBufferSource, i, j, getSideMaterial(potDecorations.back()));
      this.renderSide(this.leftSide, poseStack, multiBufferSource, i, j, getSideMaterial(potDecorations.left()));
      this.renderSide(this.rightSide, poseStack, multiBufferSource, i, j, getSideMaterial(potDecorations.right()));
    } else {
      poseStack.scale(1.0f, -1.0f, -1.0f);
      VertexConsumer vertexConsumer = DECORATED_POT_OPENED.buffer(multiBufferSource, RenderType::entitySolid);
      this.open.render(poseStack, vertexConsumer, i, j);
      this.sherds.render(poseStack, vertexConsumer, i, j);
    }

    poseStack.popPose();
  }

  private void renderSide(ModelPart modelPart, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Material material) {
    modelPart.render(poseStack, material.buffer(multiBufferSource, RenderType::entitySolid), i, j);
  }
}
