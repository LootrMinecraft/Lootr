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

  private boolean useParent;

  private UnbakedModel parent;

  public BarrelModel(ResourceLocation parent, ResourceLocation opened, ResourceLocation unopened, ResourceLocation vanilla, ResourceLocation old_unopened, ResourceLocation old_opened) {
    this(true, GuiLight.SIDE, ItemTransforms.NO_TRANSFORMS, TextureSlots.Data.EMPTY, parent, opened, unopened, vanilla, old_unopened, old_opened);
    this.useParent = true;
  }

  public BarrelModel(boolean ambientOcclusion, GuiLight guiLight, ItemTransforms transforms, TextureSlots.Data textures, ResourceLocation parent, ResourceLocation opened, ResourceLocation unopened, ResourceLocation vanilla, ResourceLocation old_unopened, ResourceLocation old_opened) {
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
  public Boolean ambientOcclusion() {
    if (this.useParent) {
      if (this.parent != null) {
        return this.parent.ambientOcclusion();
      } else {
        return true;
      }
    } else {
      return ambientOcclusion;
    }
  }

  @Nullable
  @Override
  public GuiLight guiLight() {
    if (this.useParent) {
      if (this.parent != null) {
        return this.parent.guiLight();
      } else {
        return GuiLight.SIDE;
      }
    } else {
      return guiLight;
    }
  }

  @Nullable
  @Override
  public ItemTransforms transforms() {
    if (this.useParent) {
      if (this.parent != null) {
        return this.parent.transforms();
      } else {
        return ItemTransforms.NO_TRANSFORMS;
      }
    } else {
      return itemTransforms;
    }
  }

  @Override
  public TextureSlots.Data textureSlots() {
    if (this.useParent) {
      if (this.parent != null) {
        return this.parent.textureSlots();
      } else {
        return TextureSlots.Data.EMPTY;
      }
    } else {
      return textures;
    }

  }

  @Nullable
  @Override
  public ResourceLocation parent() {
    return this.parentLocation;
  }
}

