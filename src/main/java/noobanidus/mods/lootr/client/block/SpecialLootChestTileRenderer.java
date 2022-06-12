package noobanidus.mods.lootr.client.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.block.LootrChestBlock;
import noobanidus.mods.lootr.block.tile.LootrChestTileEntity;
import noobanidus.mods.lootr.config.ConfigManager;

import java.util.UUID;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class SpecialLootChestTileRenderer<T extends LootrChestTileEntity & ILootTile> extends TileEntitySpecialRenderer<T> {
  private UUID playerId = null;

  private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
  public static final ResourceLocation MATERIAL_NOT_OPENED = new ResourceLocation(Lootr.MODID, "textures/chest.png");
  public static final ResourceLocation MATERIAL_OPENED = new ResourceLocation(Lootr.MODID, "textures/chest_opened.png");
  private final ModelLootChest simpleChest = new ModelLootChest();

  public SpecialLootChestTileRenderer() {
    super();
  }

  @Override
  public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
  {
    GlStateManager.enableDepth();
    GlStateManager.depthFunc(515);
    GlStateManager.depthMask(true);
    int i = te.hasWorld() ? te.getBlockMetadata() : 0;

    ModelChest modelchest = this.simpleChest;

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
      this.bindTexture(getChestTexture(te));
    }



      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();

      if (destroyStage < 0)
      {
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
      }

      GlStateManager.translate((float)x, (float)y + 1.0F, (float)z + 1.0F);
      GlStateManager.scale(1.0F, -1.0F, -1.0F);
      GlStateManager.translate(0.5F, 0.5F, 0.5F);
      int j = 0;

      if (i == 2)
      {
        j = 180;
      }

      if (i == 3)
      {
        j = 0;
      }

      if (i == 4)
      {
        j = 90;
      }

      if (i == 5)
      {
        j = -90;
      }


      GlStateManager.rotate((float)j, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;

      f = 1.0F - f;
      f = 1.0F - f * f * f;
      modelchest.chestLid.rotateAngleX = -(f * ((float)Math.PI / 2F));
      modelchest.renderAll();
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

  private ResourceLocation getChestTexture(T tile) {
    if (ConfigManager.isVanillaTextures()) {
      return TEXTURE_NORMAL;
    }
    if (playerId == null) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.player == null) {
        return MATERIAL_NOT_OPENED;
      } else {
        playerId = mc.player.getUniqueID();
      }
    }
    if (tile.isOpened()) {
      return MATERIAL_OPENED;
    }
    if (tile.getOpeners().contains(playerId)) {
      return MATERIAL_OPENED;
    } else {
      return MATERIAL_NOT_OPENED;
    }
  }
}
