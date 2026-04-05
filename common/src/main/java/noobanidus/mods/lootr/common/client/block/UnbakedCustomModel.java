package noobanidus.mods.lootr.common.client.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jspecify.annotations.NonNull;

import java.util.List;

public abstract class UnbakedCustomModel implements BlockStateModel.Unbaked {
  public static <T extends UnbakedCustomModel> MapCodec<T> getCodec(Provider<T> provider) {
    return RecordCodecBuilder.mapCodec(instance ->
        instance.group(Identifier.CODEC.fieldOf("opened")
                .forGetter(UnbakedCustomModel::getOpened),
            Identifier.CODEC.fieldOf("unopened")
                .forGetter(UnbakedCustomModel::getUnopened),
            Identifier.CODEC.fieldOf("vanilla")
                .forGetter(UnbakedCustomModel::getVanilla), Variant.SimpleModelState.MAP_CODEC.fieldOf("state")
                .forGetter(UnbakedCustomModel::getState),
            Codec.BOOL.fieldOf("open").forGetter(UnbakedCustomModel::getOpen)
        ).apply(instance, provider::create));
  }

  @FunctionalInterface
  public interface Provider<T extends UnbakedCustomModel> {
    T create(Identifier opened, Identifier unopened, Identifier vanilla, Variant.SimpleModelState state, boolean open);
  }

  @FunctionalInterface
  public interface Baker {
    BlockStateModel bake(BlockStateModel opened, BlockStateModel unopened, BlockStateModel vanilla, boolean open);
  }

  protected final Identifier opened, unopened, vanilla;
  protected final Variant.SimpleModelState state;
  protected final boolean open;

  public UnbakedCustomModel(Identifier opened, Identifier unopened, Identifier vanilla, Variant.SimpleModelState state, boolean open) {
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
    this.state = state;
    this.open = open;
  }

  public Identifier getOpened() {
    return opened;
  }

  public Identifier getUnopened() {
    return unopened;
  }

  public Identifier getVanilla() {
    return vanilla;
  }

  public boolean getOpen() {
    return open;
  }

  public Variant.SimpleModelState getState() {
    return state;
  }

  protected abstract Baker getBaker();

  @Override
  public @NonNull BlockStateModel bake(@NonNull ModelBaker baker) {
    return getBaker().bake(
        new SingleVariant(SimpleModelWrapper.bake(baker, opened, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, unopened, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, vanilla, state.asModelState())),
        this.open
    );
  }

  @Override
  public void resolveDependencies(Resolver resolver) {
    resolver.markDependency(opened);
    resolver.markDependency(unopened);
    resolver.markDependency(vanilla);
  }

  public record BarrelKey(boolean vanilla, boolean open, boolean visuallyOpen, int facing) {
  }

  public abstract static class Baked implements BlockStateModel {
    protected final BlockStateModel unopened, opened, vanilla;
    protected final boolean open;

    public Baked(BlockStateModel opened, BlockStateModel unopened, BlockStateModel vanilla, boolean open) {
      this.unopened = unopened;
      this.opened = opened;
      this.vanilla = vanilla;
      this.open = open;
    }

    @Override
    public void collectParts(@NonNull RandomSource random, @NonNull List<BlockStateModelPart> output) {
      if (LootrAPI.isVanillaTextures()) {
        vanilla.collectParts(random, output);
      } else {
        if (open) {
          opened.collectParts(random, output);
        } else {
          unopened.collectParts(random, output);
        }
      }
    }

    protected abstract boolean isOpenFromBATG(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random);

    public Object internalCreateObjectKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
      boolean visuallyOpen = isOpenFromBATG(level, pos, state, random);
      int facing = state.getValue(BarrelBlock.FACING).ordinal();
      boolean vanilla = LootrAPI.isVanillaTextures();
      return new BarrelKey(vanilla, open, visuallyOpen, facing);
    }
  }
}
