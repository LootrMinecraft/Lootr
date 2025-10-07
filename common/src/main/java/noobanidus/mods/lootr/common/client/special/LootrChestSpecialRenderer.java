package noobanidus.mods.lootr.common.client.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import org.jetbrains.annotations.Nullable;

public class LootrChestSpecialRenderer extends ChestSpecialRenderer {
  public LootrChestSpecialRenderer(MaterialSet materials, ChestModel chestModel, Material material, float f) {
    super(materials, chestModel, material, f);
  }

  public record Unbaked(ResourceLocation texture, ResourceLocation oldTexture, ResourceLocation vanillaTexture, float openness) implements SpecialModelRenderer.Unbaked {
    public static final MapCodec<LootrChestSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
            ResourceLocation.CODEC.fieldOf("old_texture").forGetter(Unbaked::oldTexture),
            ResourceLocation.CODEC.fieldOf("vanilla_texture").forGetter(Unbaked::vanillaTexture),
            Codec.FLOAT.fieldOf("openness").forGetter(Unbaked::openness)
        ).apply(instance, LootrChestSpecialRenderer.Unbaked::new)
    );

    public Unbaked (ResourceLocation texture, ResourceLocation oldTexture, ResourceLocation vanillaTexture) {
      this(texture, oldTexture, vanillaTexture, 0.0f);
    }

    public static LootrChestSpecialRenderer.Unbaked chest () {
      return new Unbaked(LootrChestBlockRenderer.MATERIAL.texture(), LootrChestBlockRenderer.OLD_MATERIAL.texture(), Sheets.CHEST_LOCATION.texture());
    }

    public static LootrChestSpecialRenderer.Unbaked trappedChest () {
      return new Unbaked(LootrChestBlockRenderer.MATERIAL3.texture(), LootrChestBlockRenderer.OLD_MATERIAL3.texture(), Sheets.CHEST_TRAP_LOCATION.texture());
    }

    @Nullable
    @Override
    public SpecialModelRenderer<?> bake(BakingContext context) {
      ChestModel model = new ChestModel(context.entityModelSet().bakeLayer(ModelLayers.CHEST));
      Material material;
      if (LootrAPI.isVanillaTextures()) {
        material = new Material(Sheets.CHEST_SHEET, vanillaTexture);
      } else if (LootrAPI.isOldTextures()) {
        material = new Material(Sheets.CHEST_SHEET, oldTexture);
      } else {
        material = new Material(Sheets.CHEST_SHEET, texture);
      }
      return new LootrChestSpecialRenderer(context.materials(), model, material, openness);
    }

    @Override
    public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
      return MAP_CODEC;
    }
  }
}
