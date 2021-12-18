package noobanidus.mods.lootr.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

import java.util.UUID;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class SpecialLootChestTileRenderer<T extends SpecialLootChestTile & ILootTile> extends ChestRenderer<T> {
  private UUID playerId = null;
  public static final Material MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Lootr.MODID, "chest"));
  public static final Material MATERIAL2 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Lootr.MODID, "chest_opened"));

  public SpecialLootChestTileRenderer(BlockEntityRenderDispatcher tile) {
    super(tile);
  }

  @Override
  protected Material getMaterial(T tile, ChestType type) {
    if (playerId == null) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
        return MATERIAL;
      } else {
        playerId = mc.player.getUUID();
      }
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
