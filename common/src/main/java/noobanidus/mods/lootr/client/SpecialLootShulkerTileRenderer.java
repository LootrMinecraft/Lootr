package noobanidus.mods.lootr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.blocks.LootrShulkerBlock;
import noobanidus.mods.lootr.tiles.SpecialLootShulkerTile;

import java.util.UUID;

public class SpecialLootShulkerTileRenderer extends BlockEntityRenderer<SpecialLootShulkerTile> {
  public static final Material MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Lootr.MODID, "shulker"));
  public static final Material MATERIAL2 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Lootr.MODID, "shulker_opened"));
  private final ShulkerModel<?> model = new ShulkerModel<>();
  private UUID playerId = null;

  public SpecialLootShulkerTileRenderer(BlockEntityRenderDispatcher p_i226013_2_) {
    super(p_i226013_2_);
  }

  protected Material getMaterial(SpecialLootShulkerTile tile) {
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

  @Override
  public void render(SpecialLootShulkerTile pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
    Direction direction = Direction.UP;
    if (pBlockEntity.hasLevel()) {
      BlockState blockstate = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos());
      if (blockstate.getBlock() instanceof LootrShulkerBlock) {
        direction = blockstate.getValue(LootrShulkerBlock.FACING);
      }
    }

    Material rendermaterial = getMaterial(pBlockEntity);

    pMatrixStack.pushPose();
    pMatrixStack.translate(0.5D, 0.5D, 0.5D);
    pMatrixStack.scale(0.9995F, 0.9995F, 0.9995F);
    pMatrixStack.mulPose(direction.getRotation());
    pMatrixStack.scale(1.0F, -1.0F, -1.0F);
    pMatrixStack.translate(0.0D, -1.0D, 0.0D);
    VertexConsumer ivertexbuilder = rendermaterial.buffer(pBuffer, RenderType::entityCutoutNoCull);
    this.model.getBase().render(pMatrixStack, ivertexbuilder, pCombinedLight, pCombinedOverlay);
    pMatrixStack.translate(0.0D, -pBlockEntity.getProgress(pPartialTicks) * 0.5F, 0.0D);
    pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(270.0F * pBlockEntity.getProgress(pPartialTicks)));
    this.model.getLid().render(pMatrixStack, ivertexbuilder, pCombinedLight, pCombinedOverlay);
    pMatrixStack.popPose();
  }
}
