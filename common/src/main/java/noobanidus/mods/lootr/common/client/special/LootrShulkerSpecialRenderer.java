package noobanidus.mods.lootr.common.client.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBlockRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public class LootrShulkerSpecialRenderer implements NoDataSpecialModelRenderer {
  private final LootrShulkerBlockRenderer.ShulkerBoxModel boxModel;
  private final Material material;
  private final float openness;
  private final Direction orientation;

  public LootrShulkerSpecialRenderer(LootrShulkerBlockRenderer.ShulkerBoxModel boxModel, Material material, float openness, Direction direction) {
    this.boxModel = boxModel;
    this.material = material;
    this.openness = openness;
    this.orientation = direction;
  }

  @Override
  public void render(ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, boolean bl) {
    prepareModel(poseStack, this.orientation, this.openness);
    boxModel.renderToBuffer(poseStack, material.buffer(multiBufferSource, boxModel::renderType), i, j);
    poseStack.popPose();
  }

  private void prepareModel (PoseStack poseStack, Direction orientation, float openness) {
    poseStack.pushPose();
    poseStack.translate(0.5f, 0.5f, 0.5f);
    poseStack.scale(0.9995f, 0.9995f, 0.9995f);
    poseStack.mulPose(orientation.getRotation());
    poseStack.scale(1.0f, -1.0f, -1.0f);
    poseStack.translate(0.0f, -1.0f, 0.0f);
    boxModel.animate(openness);
  }

  @Override
  public void getExtents(Set<Vector3f> p_428206_) {
    PoseStack posestack = new PoseStack();
    this.prepareModel(posestack, this.orientation, this.openness);
    this.boxModel.root().getExtentsForGui(posestack, p_428206_);
  }

  public record Unbaked(ResourceLocation texture, ResourceLocation oldTexture, float openness, Direction orientation) implements SpecialModelRenderer.Unbaked {
    public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
            ResourceLocation.CODEC.fieldOf("old_texture").forGetter(Unbaked::oldTexture),
            Codec.FLOAT.fieldOf("openness").forGetter(Unbaked::openness),
            Direction.CODEC.fieldOf("orientation").forGetter(Unbaked::orientation)
        ).apply(instance, LootrShulkerSpecialRenderer.Unbaked::new)
    );

    public Unbaked (ResourceLocation texture, ResourceLocation oldTexture) {
      this(texture, oldTexture, 0.0f, Direction.UP);
    }

    public static Unbaked shulker () {
      return new Unbaked(LootrShulkerBlockRenderer.MATERIAL.texture(), LootrShulkerBlockRenderer.MATERIAL3.texture());
    }

    @Nullable
    @Override
    public SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
      LootrShulkerBlockRenderer.ShulkerBoxModel model = new LootrShulkerBlockRenderer.ShulkerBoxModel(entityModelSet.bakeLayer(ModelLayers.SHULKER));
      Material material;
      if (LootrAPI.isVanillaTextures()) {
        material = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else if (LootrAPI.isOldTextures()) {
        material = new Material(Sheets.SHULKER_SHEET, oldTexture);
      } else {
        material = new Material(Sheets.SHULKER_SHEET, texture);
      }
      return new LootrShulkerSpecialRenderer(model, material, openness, orientation);
    }

    @Override
    public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
      return MAP_CODEC;
    }
  }
}
