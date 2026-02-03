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
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.block.LootrBarrelBlock;

import java.util.List;

public abstract class UnbakedBarrelBlockStateModel implements BlockStateModel.Unbaked {
  public static <T extends UnbakedBarrelBlockStateModel> MapCodec<T> getCodec(Provider<T> provider) {
    return RecordCodecBuilder.mapCodec(instance ->
        instance.group(Identifier.CODEC.fieldOf("unopened")
            .forGetter(UnbakedBarrelBlockStateModel::getUnopened), Identifier.CODEC.fieldOf("opened")
            .forGetter(UnbakedBarrelBlockStateModel::getOpened), Identifier.CODEC.fieldOf("vanilla")
            .forGetter(UnbakedBarrelBlockStateModel::getVanilla), Identifier.CODEC.fieldOf("old_opened")
            .forGetter(UnbakedBarrelBlockStateModel::getOldOpened), Identifier.CODEC.fieldOf("old_unopened")
            .forGetter(UnbakedBarrelBlockStateModel::getOldUnopened), Variant.SimpleModelState.MAP_CODEC.fieldOf("state")
            .forGetter(UnbakedBarrelBlockStateModel::getState)
        ).apply(instance, provider::create));
  }

  @FunctionalInterface
  public interface Provider<T extends UnbakedBarrelBlockStateModel> {
    T create(Identifier opened, Identifier unopened, Identifier vanilla, Identifier old_opened, Identifier old_unopened, Variant.SimpleModelState state);
  }

  @FunctionalInterface
  public interface Baker {
    BlockStateModel bake(BlockStateModel unopened, BlockStateModel opened, BlockStateModel vanilla, BlockStateModel old_opened, BlockStateModel old_unopened);
  }

  protected final Identifier opened, unopened, vanilla, old_opened, old_unopened;
  protected final Variant.SimpleModelState state;

  public UnbakedBarrelBlockStateModel(Identifier opened, Identifier unopened, Identifier vanilla, Identifier old_opened, Identifier old_unopened, Variant.SimpleModelState state) {
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
    this.old_opened = old_opened;
    this.old_unopened = old_unopened;
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

  public Identifier getOldOpened() {
    return old_opened;
  }

  public Identifier getOldUnopened() {
    return old_unopened;
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
        new SingleVariant(SimpleModelWrapper.bake(baker, vanilla, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, old_opened, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, old_unopened, state.asModelState()))
    );
  }

  @Override
  public void resolveDependencies(Resolver resolver) {
    resolver.markDependency(unopened);
    resolver.markDependency(opened);
    resolver.markDependency(vanilla);
    resolver.markDependency(old_unopened);
    resolver.markDependency(old_opened);
  }

  public record BarrelKey(boolean vanilla, boolean old, boolean open, boolean visuallyOpen, int facing) {
  }

  public abstract static class Baked implements BlockStateModel {
    protected final BlockStateModel unopened, opened, vanilla, old_opened, old_unopened;

    public Baked(BlockStateModel unopened, BlockStateModel opened, BlockStateModel vanilla, BlockStateModel old_opened, BlockStateModel old_unopened) {
      this.unopened = unopened;
      this.opened = opened;
      this.vanilla = vanilla;
      this.old_opened = old_opened;
      this.old_unopened = old_unopened;
    }

    @Override
    public void collectParts(RandomSource random, List<BlockModelPart> output) {
      if (LootrAPI.isVanillaTextures()) {
        vanilla.collectParts(random, output);
      } else if (LootrAPI.isOldTextures()) {
        old_unopened.collectParts(random, output);
      } else {
        unopened.collectParts(random, output);
      }
    }

    @Override
    public TextureAtlasSprite particleIcon() {
      if (LootrAPI.isVanillaTextures()) {
        return vanilla.particleIcon();
      }

      if (LootrAPI.isOldTextures()) {
        return old_unopened.particleIcon();
      }

      return unopened.particleIcon();
    }

    protected abstract boolean isOpenFromBATG(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random);

    public Object internalCreateObjectKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
      boolean visuallyOpen = isOpenFromBATG(level, pos, state, random);
      boolean open = isOpenFromBATG(level, pos, state, random);
      int facing = state.getValue(LootrBarrelBlock.FACING).ordinal();
      boolean vanilla = LootrAPI.isVanillaTextures();
      boolean old = LootrAPI.isOldTextures();
      return new BarrelKey(vanilla, old, open, visuallyOpen, facing);
    }
  }
}
