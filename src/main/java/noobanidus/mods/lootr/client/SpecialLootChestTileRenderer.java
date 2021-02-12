package noobanidus.mods.lootr.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.blocks.LootrChestBlock;
import noobanidus.mods.lootr.tiles.ILootTile;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

import java.util.UUID;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class SpecialLootChestTileRenderer<T extends SpecialLootChestTile & ILootTile> extends ChestTileEntityRenderer<T> {
  private UUID playerId = null;
  public static final RenderMaterial MATERIAL = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(Lootr.MODID, "chest"));
  public static final RenderMaterial MATERIAL2 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(Lootr.MODID, "chest_opened"));

  public SpecialLootChestTileRenderer(TileEntityRendererDispatcher tile) {
    super(tile);
  }

  @Override
  protected RenderMaterial getMaterial(T tile, ChestType type) {
    if (playerId == null) {
      playerId = Minecraft.getInstance().player.getUniqueID();
    }
    if (tile.isOpened()) {
      return MATERIAL2;
    }
    if (tile.getOpeners().contains(playerId)) {
      return MATERIAL2;
    } else {
      return MATERIAL;
    }
  }
}
