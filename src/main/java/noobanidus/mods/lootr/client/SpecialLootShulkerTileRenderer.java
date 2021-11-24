package noobanidus.mods.lootr.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.blocks.LootrShulkerBlock;
import noobanidus.mods.lootr.tiles.SpecialLootShulkerTile;

import java.util.UUID;

public class SpecialLootShulkerTileRenderer extends TileEntityRenderer<SpecialLootShulkerTile> {
  public static final RenderMaterial MATERIAL = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation(Lootr.MODID, "shulker"));
  public static final RenderMaterial MATERIAL2 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation(Lootr.MODID, "shulker_opened"));
  private final ShulkerModel<?> model = new ShulkerModel<>();
  private UUID playerId = null;

  public SpecialLootShulkerTileRenderer(TileEntityRendererDispatcher p_i226013_2_) {
    super(p_i226013_2_);
  }

  protected RenderMaterial getMaterial(SpecialLootShulkerTile tile) {
    if (playerId == null) {
      playerId = Minecraft.getInstance().player.getUUID();
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
  public void render(SpecialLootShulkerTile pBlockEntity, float pPartialTicks, MatrixStack pMatrixStack, IRenderTypeBuffer pBuffer, int pCombinedLight, int pCombinedOverlay) {
    Direction direction = Direction.UP;
    if (pBlockEntity.hasLevel()) {
      BlockState blockstate = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos());
      if (blockstate.getBlock() instanceof LootrShulkerBlock) {
        direction = blockstate.getValue(LootrShulkerBlock.FACING);
      }
    }

    RenderMaterial rendermaterial = getMaterial(pBlockEntity);

    pMatrixStack.pushPose();
    pMatrixStack.translate(0.5D, 0.5D, 0.5D);
    pMatrixStack.scale(0.9995F, 0.9995F, 0.9995F);
    pMatrixStack.mulPose(direction.getRotation());
    pMatrixStack.scale(1.0F, -1.0F, -1.0F);
    pMatrixStack.translate(0.0D, -1.0D, 0.0D);
    IVertexBuilder ivertexbuilder = rendermaterial.buffer(pBuffer, RenderType::entityCutoutNoCull);
    this.model.getBase().render(pMatrixStack, ivertexbuilder, pCombinedLight, pCombinedOverlay);
    pMatrixStack.translate(0.0D, -pBlockEntity.getProgress(pPartialTicks) * 0.5F, 0.0D);
    pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(270.0F * pBlockEntity.getProgress(pPartialTicks)));
    this.model.getLid().render(pMatrixStack, ivertexbuilder, pCombinedLight, pCombinedOverlay);
    pMatrixStack.popPose();
  }
}
