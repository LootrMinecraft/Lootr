package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.entity.PotDecorations;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LootrDecoratedPotRenderer implements BlockEntityRenderer<LootrDecoratedPotBlockEntity> {
  public static final ModelLayerLocation OPEN_POT_LAYER = new ModelLayerLocation(LootrProperties.DECORATED_POT, "main");

  private final ModelPart neck;
  private final ModelPart frontSide;
  private final ModelPart backSide;
  private final ModelPart leftSide;
  private final ModelPart rightSide;
  private final ModelPart top;
  private final ModelPart bottom;
  private final ModelPart open;

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
  }

	private static Material getSideMaterial(Optional<Item> optional) {
		if (optional.isPresent()) {
			Material material = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getPatternFromItem((Item)optional.get()));
			if (material != null) {
				return material;
			}
		}

		return Sheets.DECORATED_POT_SIDE;
	}

	public void render(LootrDecoratedPotBlockEntity decoratedPotBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
    Player player = ClientHooks.getPlayer();

		poseStack.pushPose();
		Direction direction = decoratedPotBlockEntity.getDirection();
		poseStack.translate(0.5, 0.0, 0.5);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - direction.toYRot()));
		poseStack.translate(-0.5, 0.0, -0.5);
    if (decoratedPotBlockEntity.hasOpened(player))
    {
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
    }

    // don't render this if open
    {
      VertexConsumer vertexConsumer = Sheets.DECORATED_POT_BASE.buffer(multiBufferSource, RenderType::entitySolid);
      this.neck.render(poseStack, vertexConsumer, i, j);
      this.top.render(poseStack, vertexConsumer, i, j);
      this.bottom.render(poseStack, vertexConsumer, i, j);
      PotDecorations potDecorations = decoratedPotBlockEntity.getDecorations();
      this.renderSide(this.frontSide, poseStack, multiBufferSource, i, j, getSideMaterial(potDecorations.front()));
      this.renderSide(this.backSide, poseStack, multiBufferSource, i, j, getSideMaterial(potDecorations.back()));
      this.renderSide(this.leftSide, poseStack, multiBufferSource, i, j, getSideMaterial(potDecorations.left()));
      this.renderSide(this.rightSide, poseStack, multiBufferSource, i, j, getSideMaterial(potDecorations.right()));
    }

    poseStack.popPose();
	}

	private void renderSide(ModelPart modelPart, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Material material) {
		modelPart.render(poseStack, material.buffer(multiBufferSource, RenderType::entitySolid), i, j);
	}
}
