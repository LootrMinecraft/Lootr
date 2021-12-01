package noobanidus.mods.lootr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.blocks.entities.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.init.ModBlocks;

public class LootrShulkerItemRenderer extends BlockEntityWithoutLevelRenderer {
  private static LootrShulkerItemRenderer INSTANCE = null;

  private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
  private final LootrShulkerBlockEntity tile = new LootrShulkerBlockEntity(BlockPos.ZERO, ModBlocks.CHEST.defaultBlockState());

  public LootrShulkerItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
    super(pBlockEntityRenderDispatcher, pEntityModelSet);
    this.blockEntityRenderDispatcher = pBlockEntityRenderDispatcher;
  }

  public LootrShulkerItemRenderer() {
    this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
  }

  @Override
  public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    this.blockEntityRenderDispatcher.renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
  }

  public static LootrShulkerItemRenderer getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LootrShulkerItemRenderer();
    }

    return INSTANCE;
  }
}
