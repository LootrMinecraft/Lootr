package noobanidus.mods.lootr.common.client.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.integration.decorated.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.client.block.LootrDecoratedPotRenderer;
import noobanidus.mods.lootr.common.integration.sherdsapi.SherdsIntegration;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class LootrDecoratedPotSpecialRenderer implements SpecialModelRenderer<PotDecorationsAdapter> {
  private final LootrDecoratedPotRenderer renderer;

  public LootrDecoratedPotSpecialRenderer(LootrDecoratedPotRenderer renderer) {
    this.renderer = renderer;
  }

  @Override
  public @org.jspecify.annotations.Nullable PotDecorationsAdapter extractArgument(ItemStack stack) {
    return SherdsIntegration.getAdapterFrom(stack);
  }

  @Override
  public void submit(@Nullable PotDecorationsAdapter argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
    this.renderer.submit(poseStack, submitNodeCollector, lightCoords, overlayCoords, argument, hasFoil, outlineColor);
  }

  @Override
  public void getExtents(Consumer<Vector3fc> p_470829_) {
    renderer.getExtents(p_470829_);
  }

  public record Unbaked() implements SpecialModelRenderer.Unbaked<PotDecorationsAdapter> {
    private static final Unbaked INSTANCE = new Unbaked();
    public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public SpecialModelRenderer<PotDecorationsAdapter> bake(BakingContext context) {
      return new LootrDecoratedPotSpecialRenderer(new LootrDecoratedPotRenderer(context));
    }

    @Override
    public MapCodec<? extends SpecialModelRenderer.Unbaked<PotDecorationsAdapter>> type() {
      return MAP_CODEC;
    }

    public static Unbaked decoratedPot() {
      return INSTANCE;
    }
  }
}
