package noobanidus.mods.lootr.common.client.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.client.block.LootrDecoratedPotRenderer;
import noobanidus.mods.lootr.common.integration.sherdsapi.SherdsIntegration;
import org.joml.Vector3fc;

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
  public void submit(@org.jspecify.annotations.Nullable PotDecorationsAdapter argument, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
    this.renderer.submit(poseStack, nodeCollector, packedLight, packedOverlay, argument, false, outlineColor);
  }

  @Override
  public void getExtents(Consumer<Vector3fc> p_470829_) {
    renderer.getExtents(p_470829_);
  }

  public record Unbaked() implements SpecialModelRenderer.Unbaked {
    private static final Unbaked INSTANCE = new Unbaked();
    public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public SpecialModelRenderer<?> bake(BakingContext context) {
      return new LootrDecoratedPotSpecialRenderer(new LootrDecoratedPotRenderer(context));
    }

    @Override
    public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
      return MAP_CODEC;
    }

    public static Unbaked decoratedPot () {
      return INSTANCE;
    }
  }
}
