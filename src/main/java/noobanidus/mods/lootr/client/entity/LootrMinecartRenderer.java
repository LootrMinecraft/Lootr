package noobanidus.mods.lootr.client.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noobanidus.mods.lootr.block.tile.LootrChestTileEntity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

@SideOnly(Side.CLIENT)
public class LootrMinecartRenderer extends RenderMinecart<LootrChestMinecartEntity> {
  private final LootrChestTileEntity tile = new LootrChestTileEntity();

  public LootrMinecartRenderer(RenderManager renderManagerIn) {
    super(renderManagerIn);
  }

  @Override
  protected void renderCartContents(LootrChestMinecartEntity entityIn, float partialTicks, IBlockState state) {
    tile.setOpened(entityIn.isOpened());
    TileEntityRendererDispatcher.instance.render(tile, 0, 0, 0, partialTicks);
  }
}
