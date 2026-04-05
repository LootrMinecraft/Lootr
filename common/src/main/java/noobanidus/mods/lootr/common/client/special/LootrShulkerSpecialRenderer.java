package noobanidus.mods.lootr.common.client.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBoxRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class LootrShulkerSpecialRenderer implements NoDataSpecialModelRenderer {
  private final LootrShulkerBoxRenderer renderer;
  private final SpriteId material;
  private final float openness;
  private final Direction orientation;

  public LootrShulkerSpecialRenderer(LootrShulkerBoxRenderer renderer, SpriteId material, float openness, Direction direction) {
    this.renderer = renderer;
    this.material = material;
    this.openness = openness;
    this.orientation = direction;
  }

  @Override
  public void getExtents(@NonNull Consumer<Vector3fc> consumer) {
    this.renderer.getExtents(this.orientation, this.openness, consumer);
  }

  @Override
  public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
    this.renderer.submit(poseStack, submitNodeCollector, lightCoords, overlayCoords, this.orientation, this.openness, null, this.material, outlineColor);
  }

  public record Unbaked(Identifier texture, float openness,
                        Direction orientation) implements SpecialModelRenderer.Unbaked<Void> {
    public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
            Codec.FLOAT.fieldOf("openness").forGetter(Unbaked::openness),
            Direction.CODEC.fieldOf("orientation").forGetter(Unbaked::orientation)
        ).apply(instance, Unbaked::new)
    );

    public Unbaked(Identifier texture) {
      this(texture, 0.0f, Direction.UP);
    }

    public static Unbaked shulker() {
      return new Unbaked(LootrShulkerBoxRenderer.MATERIAL.texture());
    }

    @Nullable
    @Override
    public SpecialModelRenderer<Void> bake(@NonNull BakingContext context) {
      LootrShulkerBoxRenderer model = new LootrShulkerBoxRenderer(context);
      SpriteId material;
      if (LootrAPI.isVanillaTextures()) {
        material = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else {
        material = new SpriteId(Sheets.SHULKER_SHEET, texture);
      }
      return new LootrShulkerSpecialRenderer(model, material, openness, orientation);
    }

    @Override
    public @NonNull MapCodec<? extends SpecialModelRenderer.Unbaked<Void>> type() {
      return MAP_CODEC;
    }
  }
}
