package noobanidus.mods.lootr.client;

import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

@SuppressWarnings("NullableProblems")
public class SpecialLootChestTileRenderer<T extends SpecialLootChestTile> extends ChestTileEntityRenderer<T> {
  @SuppressWarnings("deprecation")
  public static final RenderMaterial MATERIAL = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(Lootr.MODID, "chest"));

  public SpecialLootChestTileRenderer(TileEntityRendererDispatcher tile) {
    super(tile);
  }

  @Override
  protected RenderMaterial getMaterial(T tile, ChestType type) {
    return MATERIAL;
  }
}
