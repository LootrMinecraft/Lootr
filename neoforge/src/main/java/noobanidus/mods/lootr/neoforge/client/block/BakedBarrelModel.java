package noobanidus.mods.lootr.neoforge.client.block;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.BakedBarrelModelBase;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BakedBarrelModel extends BakedBarrelModelBase implements DynamicBlockStateModel {
  public BakedBarrelModel(boolean ambientOcclusion, boolean isSideLit, Material particle, BlockStateModel opened, BlockStateModel unopened, BlockStateModel vanilla, BlockStateModel old_opened, BlockStateModel old_unopened, ItemTransforms cameraTransforms) {
    super(ambientOcclusion, isSideLit, particle, opened, unopened, vanilla, old_opened, old_unopened, cameraTransforms);
  }

  @NotNull
  @Override
  public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
    BlockStateModel model;
    if (LootrAPI.isVanillaTextures()) {
      model = vanilla;
    } else {
      var extraData = level.getModelData(pos);
      if (extraData.has(ModBlockProperties.OPENED)) {
        if (extraData.get(ModBlockProperties.OPENED) == Boolean.TRUE) {
          model = LootrAPI.isOldTextures() ? old_opened : opened;
        } else {
          model = LootrAPI.isOldTextures() ? old_unopened : unopened;
        }
      } else {
        model = LootrAPI.isOldTextures() ? old_unopened : unopened;
      }
    }
    model.collectParts(level, pos, state, random, parts);
  }

  @Override
  public TextureAtlasSprite particleIcon(BlockAndTintGetter level, BlockPos pos, BlockState state) {
    if (LootrAPI.isVanillaTextures()) {
      return vanilla.particleIcon(level, pos, state);
    }
    var data = level.getModelData(pos);
    if (data.get(ModBlockProperties.OPENED) == Boolean.TRUE) {
      return LootrAPI.isOldTextures() ? old_opened.particleIcon(level, pos, state) : opened.particleIcon(level, pos, state);
    } else {
      return LootrAPI.isOldTextures() ? old_unopened.particleIcon(level, pos, state) : unopened.particleIcon(level, pos, state);
    }
  }
}
