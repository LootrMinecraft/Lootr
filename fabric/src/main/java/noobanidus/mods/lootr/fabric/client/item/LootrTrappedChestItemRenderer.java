package noobanidus.mods.lootr.fabric.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrTrappedChestBlockEntity;

public class LootrTrappedChestItemRenderer extends BlockEntityWithoutLevelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
  private static LootrTrappedChestItemRenderer INSTANCE = null;
  private final LootrTrappedChestBlockEntity tile;
  private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

  public LootrTrappedChestItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
    super(pBlockEntityRenderDispatcher, pEntityModelSet);
    this.blockEntityRenderDispatcher = pBlockEntityRenderDispatcher;
    this.tile = new LootrTrappedChestBlockEntity(BlockPos.ZERO, LootrRegistry.getTrappedChestBlock().defaultBlockState());
  }

  public LootrTrappedChestItemRenderer() {
    this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
  }

  public static LootrTrappedChestItemRenderer getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LootrTrappedChestItemRenderer();
    }

    return INSTANCE;
  }

  @Override
  public void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    getBlockEntityRenderDispatcher().renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
  }

  @Override
  public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
    renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
  }

  private BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
    if (this.blockEntityRenderDispatcher == null) {
      this.blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    return this.blockEntityRenderDispatcher;
  }
}
