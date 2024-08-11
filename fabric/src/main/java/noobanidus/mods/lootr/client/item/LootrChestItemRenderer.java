package noobanidus.mods.lootr.client.item;

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
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;

public class LootrChestItemRenderer extends BlockEntityWithoutLevelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
  private static LootrChestItemRenderer INSTANCE = null;
  private final LootrChestBlockEntity tile;
  private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

  public LootrChestItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
    super(pBlockEntityRenderDispatcher, pEntityModelSet);
    this.blockEntityRenderDispatcher = pBlockEntityRenderDispatcher;
    this.tile = new LootrChestBlockEntity(BlockPos.ZERO, LootrRegistry.getChestBlock().defaultBlockState());
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

  @Override
  public void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    getBlockEntityRenderDispatcher().renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
  }

  public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
    renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
  }

  public void renderByMinecart(LootrChestMinecartEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
    boolean open = tile.isClientOpened();
    tile.setClientOpened(entity.isClientOpened());
    getBlockEntityRenderDispatcher().renderItem(tile, matrixStack, buffer, combinedLight, OverlayTexture.NO_OVERLAY);
    tile.setClientOpened(open);
  }

  private BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
    if (this.blockEntityRenderDispatcher == null) {
      this.blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    return this.blockEntityRenderDispatcher;
  }
}
