package noobanidus.mods.lootr.client.item;

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
import noobanidus.mods.lootr.blocks.entities.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.registry.LootrBlockInit;

public class LootrShulkerItemRenderer extends BlockEntityWithoutLevelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static LootrShulkerItemRenderer INSTANCE = null;

    private BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final LootrShulkerBlockEntity tile = new LootrShulkerBlockEntity(BlockPos.ZERO, LootrBlockInit.SHULKER.get().defaultBlockState());

    public LootrShulkerItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
        this.blockEntityRenderDispatcher = pBlockEntityRenderDispatcher;
    }

    public LootrShulkerItemRenderer() {
        this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (this.blockEntityRenderDispatcher == null) {
            this.blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        }
        this.blockEntityRenderDispatcher.renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
    }

    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
    }

    public static LootrShulkerItemRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LootrShulkerItemRenderer();
        }
        return INSTANCE;
    }
}
