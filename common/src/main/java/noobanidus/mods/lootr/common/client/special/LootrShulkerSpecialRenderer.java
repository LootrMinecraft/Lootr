package noobanidus.mods.lootr.common.client.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
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
  private final LootrShulkerBlockRenderer renderer;
  private final Material material;
  private final float openness;
  private final Direction orientation;

  public LootrShulkerSpecialRenderer(LootrShulkerBlockRenderer renderer, Material material, float openness, Direction direction) {
    this.renderer = renderer;
    this.material = material;
    this.openness = openness;
    this.orientation = direction;
  }

  @Override
  public void submit(ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
    this.renderer.submit(poseStack, nodeCollector, packedLight, packedOverlay, this.orientation, this.openness, null, this.material, outlineColor);
  }

  @Override
  public void getExtents(Set<Vector3f> p_428206_) {
    this.renderer.getExtents(this.orientation, this.openness, p_428206_);
  }

  public record Unbaked(ResourceLocation texture, ResourceLocation oldTexture, float openness,
                        Direction orientation) implements SpecialModelRenderer.Unbaked {
    public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
            ResourceLocation.CODEC.fieldOf("old_texture").forGetter(Unbaked::oldTexture),
            Codec.FLOAT.fieldOf("openness").forGetter(Unbaked::openness),
            Direction.CODEC.fieldOf("orientation").forGetter(Unbaked::orientation)
        ).apply(instance, LootrShulkerSpecialRenderer.Unbaked::new)
    );

    public Unbaked(ResourceLocation texture, ResourceLocation oldTexture) {
      this(texture, oldTexture, 0.0f, Direction.UP);
    }

    public static Unbaked shulker() {
      return new Unbaked(LootrShulkerBlockRenderer.MATERIAL.texture(), LootrShulkerBlockRenderer.MATERIAL3.texture());
    }

    @Nullable
    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
      LootrShulkerBlockRenderer model = new LootrShulkerBlockRenderer(context);
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
