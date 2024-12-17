package noobanidus.mods.lootr.common.client.block;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BakedBarrelModelBase implements BakedModel {
  protected final boolean ambientOcclusion;
  protected final boolean isSideLit;
  protected final Material particle;
  protected final BakedModel opened;
  protected final BakedModel unopened;
  protected final BakedModel vanilla;
  protected final BakedModel old_opened;
  protected final BakedModel old_unopened;
  protected final ItemTransforms cameraTransforms;

  public BakedBarrelModelBase(boolean ambientOcclusion, boolean isSideLit, Material particle, BakedModel opened, BakedModel unopened, BakedModel vanilla, BakedModel old_opened, BakedModel old_unopened, ItemTransforms cameraTransforms) {
    this.isSideLit = isSideLit;
    this.cameraTransforms = cameraTransforms;
    this.ambientOcclusion = ambientOcclusion;
    this.particle = particle;
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
    this.old_opened = old_opened;
    this.old_unopened = old_unopened;
  }


  @Override
  public boolean useAmbientOcclusion() {
    return ambientOcclusion;
  }

  @Override
  public boolean isGui3d() {
    return true;
  }

  @Override
  public boolean usesBlockLight() {
    return isSideLit;
  }

  @Override
  public TextureAtlasSprite getParticleIcon() {
    if (LootrAPI.isVanillaTextures()) {
      return vanilla.getParticleIcon();
    } else {
      return LootrAPI.isOldTextures() ? old_opened.getParticleIcon() : opened.getParticleIcon();
    }
  }

  @Override
  public ItemTransforms getTransforms() {
    return cameraTransforms;
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
    if (LootrAPI.isVanillaTextures()) {
      return vanilla.getQuads(state, side, rand);
    } else if (LootrAPI.isNewTextures()) {
      return unopened.getQuads(state, side, rand);
    } else {
      return old_unopened.getQuads(state, side, rand);
    }
  }
}

