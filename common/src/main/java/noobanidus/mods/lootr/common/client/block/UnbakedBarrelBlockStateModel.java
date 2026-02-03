package noobanidus.mods.lootr.common.client.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.LootrBarrelBlock;

import java.util.List;

public abstract class UnbakedBarrelBlockStateModel implements BlockStateModel.Unbaked {
  public static <T extends UnbakedBarrelBlockStateModel> MapCodec<T> getCodec(Provider<T> provider) {
    return RecordCodecBuilder.mapCodec(instance ->
        instance.group(Identifier.CODEC.fieldOf("unopened")
            .forGetter(UnbakedBarrelBlockStateModel::getUnopened), Identifier.CODEC.fieldOf("opened")
            .forGetter(UnbakedBarrelBlockStateModel::getOpened), Identifier.CODEC.fieldOf("vanilla").forGetter(UnbakedBarrelBlockStateModel::getVanilla), Variant.SimpleModelState.MAP_CODEC.fieldOf("state")
            .forGetter(UnbakedBarrelBlockStateModel::getState)
        ).apply(instance, provider::create));
  }

  @FunctionalInterface
  public interface Provider<T extends UnbakedBarrelBlockStateModel> {
    T create(Identifier opened, Identifier unopened, Identifier vanilla, Variant.SimpleModelState state);
  }

  @FunctionalInterface
  public interface Baker {
    BlockStateModel bake(BlockStateModel unopened, BlockStateModel opened, BlockStateModel vanilla);
  }

  protected final Identifier opened, unopened, vanilla;
  protected final Variant.SimpleModelState state;

  public UnbakedBarrelBlockStateModel(Identifier opened, Identifier unopened, Identifier vanilla, Variant.SimpleModelState state) {
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
    this.state = state;
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

  public Variant.SimpleModelState getState() {
    return state;
  }

  protected abstract Baker getBaker();

  @Override
  public BlockStateModel bake(ModelBaker baker) {
    return getBaker().bake(
        new SingleVariant(SimpleModelWrapper.bake(baker, unopened, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, opened, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, vanilla, state.asModelState()))
    );
  }

  @Override
  public void resolveDependencies(Resolver resolver) {
    resolver.markDependency(unopened);
    resolver.markDependency(opened);
    resolver.markDependency(vanilla);
  }

  public record BarrelKey(boolean vanilla, boolean open, boolean visuallyOpen, int facing) {
  }

  public abstract static class Baked implements BlockStateModel {
    protected final BlockStateModel unopened, opened, vanilla;

    public Baked(BlockStateModel unopened, BlockStateModel opened, BlockStateModel vanilla) {
      this.unopened = unopened;
      this.opened = opened;
      this.vanilla = vanilla;
    }

    @Override
    public void collectParts(RandomSource random, List<BlockModelPart> output) {
      if (LootrAPI.isVanillaTextures()) {
        vanilla.collectParts(random, output);
      } else {
        unopened.collectParts(random, output);
      }
    }

    @Override
    public TextureAtlasSprite particleIcon() {
      if (LootrAPI.isVanillaTextures()) {
        return vanilla.particleIcon();
      }

      return unopened.particleIcon();
    }

    protected abstract boolean isOpenFromBATG(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random);

    public Object internalCreateObjectKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
      boolean visuallyOpen = isOpenFromBATG(level, pos, state, random);
      boolean open = state.getValue(BarrelBlock.OPEN);
      int facing = state.getValue(BarrelBlock.FACING).ordinal();
      boolean vanilla = LootrAPI.isVanillaTextures();
      return new BarrelKey(vanilla, open, visuallyOpen, facing);
    }
  }
}
