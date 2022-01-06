package noobanidus.mods.lootr.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.block.entities.LootrChestBlockEntity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;

public class LootrChestItemRenderer extends BlockEntityWithoutLevelRenderer {
  private static LootrChestItemRenderer INSTANCE = null;

  private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
  private final LootrChestBlockEntity tile = new LootrChestBlockEntity(BlockPos.ZERO, ModBlocks.CHEST.defaultBlockState());

  public LootrChestItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
    super(pBlockEntityRenderDispatcher, pEntityModelSet);
    this.blockEntityRenderDispatcher = pBlockEntityRenderDispatcher;
  }

  public LootrChestItemRenderer() {
    this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
  }

  @Override
  public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    this.blockEntityRenderDispatcher.renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
  }

  public void renderByMinecart (LootrChestMinecartEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
    boolean open = tile.isOpened();
    tile.setOpened(entity.isOpened());
    this.blockEntityRenderDispatcher.renderItem(tile, matrixStack, buffer, combinedLight, OverlayTexture.NO_OVERLAY);
    tile.setOpened(open);
  }

  public static LootrChestItemRenderer getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LootrChestItemRenderer();
    }

    return INSTANCE;
  }
}
