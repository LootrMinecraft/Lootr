package noobanidus.mods.lootr.common.client.block;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.RandomSource;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.List;

public abstract class BakedBarrelModelBase implements BlockStateModel {
  protected final boolean ambientOcclusion;
  protected final boolean isSideLit;
  protected final Material particle;
  protected final BlockStateModel opened;
  protected final BlockStateModel unopened;
  protected final BlockStateModel vanilla;
  protected final BlockStateModel old_opened;
  protected final BlockStateModel old_unopened;
  protected final ItemTransforms cameraTransforms;

  public BakedBarrelModelBase(boolean ambientOcclusion, boolean isSideLit, Material particle, BlockStateModel opened, BlockStateModel unopened, BlockStateModel vanilla, BlockStateModel old_opened, BlockStateModel old_unopened, ItemTransforms cameraTransforms) {
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
  public TextureAtlasSprite particleIcon() {
    if (LootrAPI.isVanillaTextures()) {
      return vanilla.particleIcon();
    } else {
      return LootrAPI.isOldTextures() ? old_opened.particleIcon() : opened.particleIcon();
    }
  }

  @Override
  public void collectParts(RandomSource randomSource, List<BlockModelPart> list) {
    if (LootrAPI.isVanillaTextures()) {
      vanilla.collectParts(randomSource, list);
    } else if (LootrAPI.isNewTextures()) {
      unopened.collectParts(randomSource, list);
    } else {
      old_unopened.collectParts(randomSource, list);
    }
  }
}

