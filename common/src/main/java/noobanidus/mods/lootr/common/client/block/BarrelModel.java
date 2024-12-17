package noobanidus.mods.lootr.common.client.block;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BarrelModel implements UnbakedModel {
  private final ResourceLocation opened;
  private final ResourceLocation unopened;
  private final ResourceLocation vanilla;
  private final ResourceLocation old_opened;
  private final ResourceLocation old_unopened;
  private final ResourceLocation parentLocation;

  private final boolean ambientOcclusion;
  private final GuiLight guiLight;
  private final ItemTransforms itemTransforms;
  private final TextureSlots.Data textures;

  private final BakedBarrelModelBuilder builder;

  private boolean useParent;

  private UnbakedModel parent;

  public BarrelModel(BakedBarrelModelBuilder builder, ResourceLocation parent, ResourceLocation opened, ResourceLocation unopened, ResourceLocation vanilla, ResourceLocation old_unopened, ResourceLocation old_opened) {
    this(builder, true, GuiLight.SIDE, ItemTransforms.NO_TRANSFORMS, TextureSlots.Data.EMPTY, parent, opened, unopened, vanilla, old_unopened, old_opened);
    this.useParent = true;
  }

  public BarrelModel(BakedBarrelModelBuilder builder, boolean ambientOcclusion, GuiLight guiLight, ItemTransforms transforms, TextureSlots.Data textures, ResourceLocation parent, ResourceLocation opened, ResourceLocation unopened, ResourceLocation vanilla, ResourceLocation old_unopened, ResourceLocation old_opened) {
    this.builder = builder;
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
    this.old_opened = old_opened;
    this.old_unopened = old_unopened;
    this.parentLocation = parent;
    this.ambientOcclusion = ambientOcclusion;
    this.guiLight = guiLight;
    this.itemTransforms = transforms;
    this.textures = textures;
    this.useParent = false;
  }

  @Nullable
  @Override
  public Boolean getAmbientOcclusion() {
    if (this.useParent) {
      if (this.parent != null) {
        return this.parent.getAmbientOcclusion();
      } else {
        return true;
      }
    } else {
      return ambientOcclusion;
    }
  }

  @Nullable
  @Override
  public GuiLight getGuiLight() {
    if (this.useParent) {
      if (this.parent != null) {
        return this.parent.getGuiLight();
      } else {
        return GuiLight.SIDE;
      }
    } else {
      return guiLight;
    }
  }

  @Nullable
  @Override
  public ItemTransforms getTransforms() {
    if (this.useParent) {
      if (this.parent != null) {
        return this.parent.getTransforms();
      } else {
        return ItemTransforms.NO_TRANSFORMS;
      }
    } else {
      return itemTransforms;
    }
  }

  @Override
  public TextureSlots.Data getTextureSlots() {
    if (this.useParent) {
      if (this.parent != null) {
        return this.parent.getTextureSlots();
      } else {
        return TextureSlots.Data.EMPTY;
      }
    } else {
      return textures;
    }

  }

  @Override
  public BakedModel bake(TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms itemTransforms) {
    return builder.build(useAmbientOcclusion, usesBlockLight, textures.getMaterial("particle"), baker.bake(opened, modelState), baker.bake(unopened, modelState), baker.bake(vanilla, modelState), baker.bake(old_opened, modelState), baker.bake(old_unopened, modelState), itemTransforms);
  }

  @Override
  public void resolveDependencies(UnbakedModel.Resolver modelGetter) {
    modelGetter.resolve(opened);
    modelGetter.resolve(unopened);
    modelGetter.resolve(vanilla);
    modelGetter.resolve(old_opened);
    modelGetter.resolve(old_unopened);
    this.parent = modelGetter.resolve(parentLocation);
  }

  @Nullable
  @Override
  public UnbakedModel getParent() {
    return this.parent;
  }

  @FunctionalInterface
  public interface BakedBarrelModelBuilder {
    BakedModel build(boolean ambientOcclusion, boolean isSideLit, Material particle, BakedModel opened, BakedModel unopened, BakedModel vanilla, BakedModel old_opened, BakedModel old_unopened, ItemTransforms cameraTransforms);
  }
}

