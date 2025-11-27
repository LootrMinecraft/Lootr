package net.zestyblaze.lootr.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.zestyblaze.lootr.block.entities.LootrChestBlockEntity;
import net.zestyblaze.lootr.entity.LootrChestMinecartEntity;
import net.zestyblaze.lootr.init.ModBlocks;

public class LootrChestItemRenderer extends BlockEntityWithoutLevelRenderer {
  private static LootrChestItemRenderer INSTANCE = null;

  private BlockEntityRenderDispatcher blockEntityRenderDispatcher;
  private final LootrChestBlockEntity tile;

  public LootrChestItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
    super(pBlockEntityRenderDispatcher, pEntityModelSet);
    this.blockEntityRenderDispatcher = pBlockEntityRenderDispatcher;
    this.tile = new LootrChestBlockEntity(BlockPos.ZERO, ModBlocks.CHEST.defaultBlockState());
  }

  public LootrChestItemRenderer() {
    this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
  }

  public static LootrChestItemRenderer getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LootrChestItemRenderer();
    }

    return INSTANCE;
  }

  public BuiltinItemRendererRegistry.DynamicItemRenderer getDynamicItemRenderer() {
    return this::renderByItem;
  }

  @Override
  public void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    getBlockEntityRenderDispatcher().renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
  }

  public void renderByMinecart(LootrChestMinecartEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
    boolean open = tile.isOpened();
    tile.setOpened(entity.isOpened());
    getBlockEntityRenderDispatcher().renderItem(tile, matrixStack, buffer, combinedLight, OverlayTexture.NO_OVERLAY);
    tile.setOpened(open);
  }

  private BlockEntityRenderDispatcher getBlockEntityRenderDispatcher () {
    if (this.blockEntityRenderDispatcher == null) {
      this.blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    return this.blockEntityRenderDispatcher;
  }
}
