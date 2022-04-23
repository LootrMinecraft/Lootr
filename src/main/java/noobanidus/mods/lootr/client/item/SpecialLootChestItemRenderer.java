package noobanidus.mods.lootr.client.item;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import noobanidus.mods.lootr.block.tile.LootrChestTileEntity;

public class SpecialLootChestItemRenderer extends TileEntityItemStackRenderer {
  private final LootrChestTileEntity tile = new LootrChestTileEntity();

  @Override
  public void renderByItem(ItemStack itemStackIn, float partialTicks) {
    TileEntityRendererDispatcher.instance.render(tile, 0, 0, 0, partialTicks);
  }
}
