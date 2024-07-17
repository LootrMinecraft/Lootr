package noobanidus.mods.lootr.common.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;

public class LootrShulkerItemRenderer extends BlockEntityWithoutLevelRenderer {
  private static LootrShulkerItemRenderer INSTANCE = null;

  private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
  private final LootrShulkerBlockEntity blockEntity;

  public LootrShulkerItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
    super(pBlockEntityRenderDispatcher, pEntityModelSet);
    this.blockEntityRenderDispatcher = pBlockEntityRenderDispatcher;
    this.blockEntity = new LootrShulkerBlockEntity(BlockPos.ZERO, LootrRegistry.getShulkerBlock().defaultBlockState());
  }

  public LootrShulkerItemRenderer() {
    this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
  }

  public static LootrShulkerItemRenderer getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LootrShulkerItemRenderer();
    }

    return INSTANCE;
  }

  @Override
  public void renderByItem(ItemStack p_108830_, ItemDisplayContext p_270899_, PoseStack p_108832_, MultiBufferSource p_108833_, int p_108834_, int p_108835_) {
    this.blockEntityRenderDispatcher.renderItem(blockEntity, p_108832_, p_108833_, p_108834_, p_108835_);
  }
}
