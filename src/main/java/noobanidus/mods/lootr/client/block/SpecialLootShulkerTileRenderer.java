package noobanidus.mods.lootr.client.block;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShulker;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderShulker;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.tile.LootrShulkerTileEntity;
import noobanidus.mods.lootr.config.ConfigManager;

import java.util.UUID;

public class SpecialLootShulkerTileRenderer extends TileEntitySpecialRenderer<LootrShulkerTileEntity> {
  public static final ResourceLocation MATERIAL = new ResourceLocation(Lootr.MODID, "textures/shulker.png");
  public static final ResourceLocation MATERIAL2 = new ResourceLocation(Lootr.MODID, "textures/shulker_opened.png");
  private final ModelShulker model = new ModelShulker();
  private UUID playerId = null;

  public SpecialLootShulkerTileRenderer() {
    super();
  }

  protected ResourceLocation getMaterial(LootrShulkerTileEntity tile) {
    if (ConfigManager.isVanillaTextures()) {
      return RenderShulker.SHULKER_ENDERGOLEM_TEXTURE[tile.getColor().getMetadata()];
    }
    if (playerId == null) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.player == null) {
        return MATERIAL;
      } else {
        playerId = mc.player.getUniqueID();
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

  @Override
  public void render(LootrShulkerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
  {
    EnumFacing enumfacing = EnumFacing.UP;

    if (te.hasWorld())
    {
      IBlockState iblockstate = this.getWorld().getBlockState(te.getPos());

      if (iblockstate.getBlock() instanceof BlockShulkerBox)
      {
        enumfacing = (EnumFacing)iblockstate.getValue(BlockShulkerBox.FACING);
      }
    }

    GlStateManager.enableDepth();
    GlStateManager.depthFunc(515);
    GlStateManager.depthMask(true);
    GlStateManager.disableCull();

    if (destroyStage >= 0)
    {
      this.bindTexture(DESTROY_STAGES[destroyStage]);
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      GlStateManager.scale(4.0F, 4.0F, 1.0F);
      GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
      GlStateManager.matrixMode(5888);
    }
    else
    {
      this.bindTexture(getMaterial(te));
    }

    GlStateManager.pushMatrix();
    GlStateManager.enableRescaleNormal();

    if (destroyStage < 0)
    {
      GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
    }

    GlStateManager.translate((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
    GlStateManager.scale(1.0F, -1.0F, -1.0F);
    GlStateManager.translate(0.0F, 1.0F, 0.0F);
    float f = 0.9995F;
    GlStateManager.scale(0.9995F, 0.9995F, 0.9995F);
    GlStateManager.translate(0.0F, -1.0F, 0.0F);

    switch (enumfacing)
    {
      case DOWN:
        GlStateManager.translate(0.0F, 2.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      case UP:
      default:
        break;
      case NORTH:
        GlStateManager.translate(0.0F, 1.0F, 1.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        break;
      case SOUTH:
        GlStateManager.translate(0.0F, 1.0F, -1.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        break;
      case WEST:
        GlStateManager.translate(-1.0F, 1.0F, 0.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
        break;
      case EAST:
        GlStateManager.translate(1.0F, 1.0F, 0.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
    }

    this.model.base.render(0.0625F);
    GlStateManager.translate(0.0F, -te.getProgress(partialTicks) * 0.5F, 0.0F);
    GlStateManager.rotate(270.0F * te.getProgress(partialTicks), 0.0F, 1.0F, 0.0F);
    this.model.lid.render(0.0625F);
    GlStateManager.enableCull();
    GlStateManager.disableRescaleNormal();
    GlStateManager.popMatrix();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    if (destroyStage >= 0)
    {
      GlStateManager.matrixMode(5890);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
    }
  }
}
