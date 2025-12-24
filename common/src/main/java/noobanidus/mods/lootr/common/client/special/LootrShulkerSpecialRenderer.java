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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBoxRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Set;
import java.util.function.Consumer;

public class LootrShulkerSpecialRenderer implements NoDataSpecialModelRenderer {
  private final LootrShulkerBoxRenderer renderer;
  private final Material material;
  private final float openness;
  private final Direction orientation;

  public LootrShulkerSpecialRenderer(LootrShulkerBoxRenderer renderer, Material material, float openness, Direction direction) {
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
  public void getExtents(Consumer<Vector3fc> consumer) {
    this.renderer.getExtents(this.orientation, this.openness, consumer);
  }

  public record Unbaked(Identifier texture, Identifier oldTexture, float openness,
                        Direction orientation) implements SpecialModelRenderer.Unbaked {
    public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
            Identifier.CODEC.fieldOf("old_texture").forGetter(Unbaked::oldTexture),
            Codec.FLOAT.fieldOf("openness").forGetter(Unbaked::openness),
            Direction.CODEC.fieldOf("orientation").forGetter(Unbaked::orientation)
        ).apply(instance, LootrShulkerSpecialRenderer.Unbaked::new)
    );

    public Unbaked(Identifier texture, Identifier oldTexture) {
      this(texture, oldTexture, 0.0f, Direction.UP);
    }

    public static Unbaked shulker() {
      return new Unbaked(LootrShulkerBoxRenderer.MATERIAL.texture(), LootrShulkerBoxRenderer.MATERIAL3.texture());
    }

    @Nullable
    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
      LootrShulkerBoxRenderer model = new LootrShulkerBoxRenderer(context);
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
