package noobanidus.mods.lootr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

public class SpecialLootChestItemRenderer extends BlockEntityWithoutLevelRenderer {
  private final SpecialLootChestTile tile = new SpecialLootChestTile();

  @Override
  public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    BlockEntityRenderDispatcher.instance.renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
  }
}
