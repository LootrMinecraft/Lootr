package noobanidus.mods.lootr.client;

import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.model.ChestModel;
import net.minecraft.util.ResourceLocation;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

@SuppressWarnings("NullableProblems")
public class SpecialLootChestTileRenderer<T extends SpecialLootChestTile> extends ChestTileEntityRenderer<T> {
   private static final ResourceLocation TEXTURE = new ResourceLocation(Lootr.MODID, "textures/chest.png");

  @Override
  public ChestModel getChestModel(T tile, int destroyStage, boolean doubleChest) {
    if (doubleChest || destroyStage != -1 || !tile.isSpecialLootChest()) {
      return super.getChestModel(tile, destroyStage, doubleChest);
    }

    ChestModel model = super.getChestModel(tile, destroyStage, doubleChest);
    this.bindTexture(TEXTURE);
    return model;
  }
}
