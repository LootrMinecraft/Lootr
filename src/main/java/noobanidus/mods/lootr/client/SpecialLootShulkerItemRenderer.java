package noobanidus.mods.lootr.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.tiles.SpecialLootShulkerTile;

public class SpecialLootShulkerItemRenderer extends ItemStackTileEntityRenderer {
  private final SpecialLootShulkerTile tile = new SpecialLootShulkerTile();

  @Override
  public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
    TileEntityRendererDispatcher.instance.renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
  }
}
