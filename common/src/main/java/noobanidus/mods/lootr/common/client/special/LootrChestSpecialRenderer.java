package noobanidus.mods.lootr.common.client.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;

public class LootrChestSpecialRenderer extends ChestSpecialRenderer {
  public LootrChestSpecialRenderer(SpriteGetter materials, ChestModel chestModel, SpriteId material, float f) {
    super(materials, chestModel, material, f);
  }

  public record Unbaked(Identifier texture, Identifier vanillaTexture,
                        float openness) implements SpecialModelRenderer.Unbaked<Void> {
    public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
            Identifier.CODEC.fieldOf("vanilla_texture").forGetter(Unbaked::vanillaTexture),
            Codec.FLOAT.fieldOf("openness").forGetter(Unbaked::openness)
        ).apply(instance, Unbaked::new)
    );

    public Unbaked(Identifier texture, Identifier vanillaTexture) {
      this(texture, vanillaTexture, 0.0f);
    }

    public static Unbaked chest() {
      return new Unbaked(LootrChestBlockRenderer.MATERIAL.texture(), Sheets.CHEST_REGULAR.single().texture());
    }

    public static Unbaked trappedChest() {
      return new Unbaked(LootrChestBlockRenderer.MATERIAL3.texture(), Sheets.CHEST_TRAPPED.single().texture());
    }

    @Override
    public SpecialModelRenderer<Void> bake(BakingContext context) {
      ChestModel model = new ChestModel(context.entityModelSet().bakeLayer(ModelLayers.CHEST));
      SpriteId material;
      if (LootrAPI.isVanillaTextures()) {
        material = new SpriteId(Sheets.CHEST_SHEET, vanillaTexture);
      } else {
        material = new SpriteId(Sheets.CHEST_SHEET, texture);
      }
      return new LootrChestSpecialRenderer(context.sprites(), model, material, openness);
    }

    @Override
    public MapCodec<? extends SpecialModelRenderer.Unbaked<Void>> type() {
      return MAP_CODEC;
    }
  }
}
