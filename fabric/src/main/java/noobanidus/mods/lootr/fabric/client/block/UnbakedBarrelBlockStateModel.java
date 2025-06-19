package noobanidus.mods.lootr.fabric.client.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.FacingUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class UnbakedBarrelBlockStateModel implements CustomUnbakedBlockStateModel {
  public static final UnbakedBarrelBlockStateModel INSTANCE = new UnbakedBarrelBlockStateModel();
  public static MapCodec<UnbakedBarrelBlockStateModel> CODEC = MapCodec.unit(INSTANCE);

  // Unopened models
  private static final ResourceLocation LOOTR_BARREL_UNOPENED = LootrAPI.rl("block/lootr_barrel_unopened");
  private static final ResourceLocation LOOTR_BARREL_UNOPENED_OPEN = LootrAPI.rl("block/lootr_barrel_unopened_open");

  // Opened models
  private static final ResourceLocation LOOTR_OPENED_BARREL = LootrAPI.rl("block/lootr_opened_barrel");
  private static final ResourceLocation LOOTR_OPENED_BARREL_OPEN = LootrAPI.rl("block/lootr_opened_barrel_open");

  // Vanilla models
  private static final ResourceLocation VANILLA = ResourceLocation.fromNamespaceAndPath("minecraft", "block/barrel");
  private static final ResourceLocation VANILLA_OPEN = ResourceLocation.fromNamespaceAndPath("minecraft", "block/barrel_open");

  // Old unopened models
  private static final ResourceLocation OLD_LOOTR_BARREL_UNOPENED = LootrAPI.rl("block/old_lootr_barrel_unopened");
  private static final ResourceLocation OLD_LOOTR_BARREL_UNOPENED_OPEN = LootrAPI.rl("block/old_lootr_barrel_unopened_open");

  private static final ResourceLocation OLD_LOOTR_OPENED_BARREL = LootrAPI.rl("block/old_lootr_opened_barrel");
  private static final ResourceLocation OLD_LOOTR_OPENED_BARREL_OPEN = LootrAPI.rl("block/old_lootr_opened_barrel_open");

  @Override
  public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
    return CODEC;
  }

  @Override
  public BlockStateModel bake(ModelBaker modelBaker) {
    BlockStateModel[] lootrBarrelUnopened = bakeDirectionalVariants(modelBaker, LOOTR_BARREL_UNOPENED);
    BlockStateModel[] lootrBarrelUnopenedOpen = bakeDirectionalVariants(modelBaker, LOOTR_BARREL_UNOPENED_OPEN);
    BlockStateModel[] lootrOpenedBarrel = bakeDirectionalVariants(modelBaker, LOOTR_OPENED_BARREL);
    BlockStateModel[] lootrOpenedBarrelOpen = bakeDirectionalVariants(modelBaker, LOOTR_OPENED_BARREL_OPEN);

    BlockStateModel[] vanilla = bakeDirectionalVariants(modelBaker, VANILLA);
    BlockStateModel[] vanillaOpen = bakeDirectionalVariants(modelBaker, VANILLA_OPEN);

    BlockStateModel[] oldLootrBarrelUnopened = bakeDirectionalVariants(modelBaker, OLD_LOOTR_BARREL_UNOPENED);
    BlockStateModel[] oldLootrBarrelUnopenedOpen = bakeDirectionalVariants(modelBaker, OLD_LOOTR_BARREL_UNOPENED_OPEN);
    BlockStateModel[] oldLootrOpenedBarrel = bakeDirectionalVariants(modelBaker, OLD_LOOTR_OPENED_BARREL);
    BlockStateModel[] oldLootrOpenedBarrelOpen = bakeDirectionalVariants(modelBaker, OLD_LOOTR_OPENED_BARREL_OPEN);


    return new Baked(lootrBarrelUnopened, lootrBarrelUnopenedOpen, lootrOpenedBarrel, lootrOpenedBarrelOpen, vanilla, vanillaOpen, oldLootrBarrelUnopened, oldLootrBarrelUnopenedOpen, oldLootrOpenedBarrel, oldLootrOpenedBarrelOpen);
  }

  @Override
  public void resolveDependencies(Resolver resolver) {
    resolver.markDependency(LOOTR_BARREL_UNOPENED);
    resolver.markDependency(LOOTR_BARREL_UNOPENED_OPEN);
    resolver.markDependency(LOOTR_OPENED_BARREL);
    resolver.markDependency(LOOTR_OPENED_BARREL_OPEN);
    resolver.markDependency(VANILLA);
    resolver.markDependency(VANILLA_OPEN);
    resolver.markDependency(OLD_LOOTR_BARREL_UNOPENED);
    resolver.markDependency(OLD_LOOTR_BARREL_UNOPENED_OPEN);
    resolver.markDependency(OLD_LOOTR_OPENED_BARREL);
    resolver.markDependency(OLD_LOOTR_OPENED_BARREL_OPEN);
  }

  public static BlockStateModel[] bakeDirectionalVariants(ModelBaker baker, ResourceLocation model) {
    BlockStateModel[] result = new BlockStateModel[6];
    for (Direction dir : Direction.values()) {
      result[dir.ordinal()] = new SingleVariant(SimpleModelWrapper.bake(baker, model, FacingUtil.transformFor(dir)));
    }
    return result;
  }

  public record BarrelKey(boolean vanilla, boolean old, boolean open, boolean visuallyOpen, int facing) {
  }

  public record Baked(BlockStateModel[] lootrBarrelUnopened, BlockStateModel[] lootrBarrelUnopenedOpen,
                      BlockStateModel[] lootrOpenedBarrel, BlockStateModel[] lootrOpenedBarrelOpen,
                      BlockStateModel[] vanilla, BlockStateModel[] vanillaOpen,
                      BlockStateModel[] oldLootrBarrelUnopened, BlockStateModel[] oldLootrBarrelUnopenedOpen,
                      BlockStateModel[] oldLootrOpenedBarrel,
                      BlockStateModel[] oldLootrOpenedBarrelOpen) implements BlockStateModel {
    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
      boolean visuallyOpen = blockView.getBlockEntityRenderData(pos) == Boolean.TRUE;
      boolean open = state.getValue(BarrelBlock.OPEN);
      int facing = state.getValue(BarrelBlock.FACING).ordinal();

      // Use the above to determine which model collection to use
      BlockStateModel[] modelCollection;

      if (LootrAPI.isVanillaTextures()) {
        modelCollection = open ? vanillaOpen : vanilla;
      } else if (LootrAPI.isOldTextures()) {
        if (visuallyOpen) {
          modelCollection = open ? oldLootrOpenedBarrelOpen : oldLootrOpenedBarrel;
        } else {
          modelCollection = open ? oldLootrBarrelUnopenedOpen : oldLootrBarrelUnopened;
        }
      } else {
        if (visuallyOpen) {
          modelCollection = open ? lootrOpenedBarrelOpen : lootrOpenedBarrel;
        } else {
          modelCollection = open ? lootrBarrelUnopenedOpen : lootrBarrelUnopened;
        }
      }

      // Use the facing to determine which model to emit
      modelCollection[facing].emitQuads(emitter, blockView, pos, state, random, cullTest);
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random) {
      boolean visuallyOpen = blockView.getBlockEntityRenderData(pos) == Boolean.TRUE;
      boolean open = state.getValue(BarrelBlock.OPEN);
      int facing = state.getValue(BarrelBlock.FACING).ordinal();
      boolean vanilla = LootrAPI.isVanillaTextures();
      boolean old = LootrAPI.isOldTextures();
      return new BarrelKey(vanilla, old, open, visuallyOpen, facing);
    }

    @Override
    public void collectParts(RandomSource randomSource, List<BlockModelPart> list) {
      // We never actually need to collect any parts as overriding Fabric's `emitQuads` means this method is never called.
      // In theory.
      // Hopefully.
    }

    @Override
    public TextureAtlasSprite particleIcon() {
      if (LootrAPI.isVanillaTextures()) {
        return vanilla[Direction.DOWN.ordinal()].particleIcon();
      }
      if (LootrAPI.isOldTextures()) {
        return oldLootrBarrelUnopened[Direction.DOWN.ordinal()].particleIcon();
      }
      return lootrBarrelUnopened[Direction.DOWN.ordinal()].particleIcon();
    }
  }
}
