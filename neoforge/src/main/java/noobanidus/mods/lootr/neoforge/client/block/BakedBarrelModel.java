package noobanidus.mods.lootr.neoforge.client.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.BakedBarrelModelBase;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakedBarrelModel extends BakedBarrelModelBase implements IDynamicBakedModel {
  public BakedBarrelModel(boolean ambientOcclusion, boolean isSideLit, Material particle, BakedModel opened, BakedModel unopened, BakedModel vanilla, BakedModel old_opened, BakedModel old_unopened, ItemTransforms cameraTransforms) {
    super(ambientOcclusion, isSideLit, particle, opened, unopened, vanilla, old_opened, old_unopened, cameraTransforms);
  }

  @NotNull
  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @NotNull RenderType renderType) {
    BakedModel model;
    if (LootrAPI.isVanillaTextures()) {
      model = vanilla;
    } else {
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
    return model.getQuads(state, side, rand, extraData, renderType);
  }

  @Override
  public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
    if (LootrAPI.isVanillaTextures()) {
      return vanilla.getParticleIcon();
    }
    if (data.get(ModBlockProperties.OPENED) == Boolean.TRUE) {
      return LootrAPI.isOldTextures() ? old_opened.getParticleIcon() : opened.getParticleIcon();
    } else {
      return LootrAPI.isOldTextures() ? old_unopened.getParticleIcon() : unopened.getParticleIcon();
    }
  }
}
