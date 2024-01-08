package noobanidus.mods.lootr.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.blocks.entities.LootrChestBlockEntity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.registry.LootrBlockInit;

public class LootrChestItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final LootrChestItemRenderer INSTANCE = new LootrChestItemRenderer();

    private final BlockEntityRenderDispatcher dispatcher;
    private final LootrChestBlockEntity blockEntity;

    public LootrChestItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
        this.dispatcher = dispatcher;
        this.blockEntity = new LootrChestBlockEntity(BlockPos.ZERO, LootrBlockInit.CHEST.get().defaultBlockState());
    }

    public LootrChestItemRenderer() {
        this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack matrixStack, MultiBufferSource bufferSource, int i, int j) {
        this.dispatcher.renderItem(this.blockEntity, matrixStack, bufferSource, i, j);
    }

    public void renderByMinecart(LootrChestMinecartEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
        boolean open = this.blockEntity.isOpened();
        this.blockEntity.setOpened(entity.isOpened());
        this.dispatcher.renderItem(this.blockEntity, matrixStack, buffer, combinedLight, OverlayTexture.NO_OVERLAY);
        this.blockEntity.setOpened(open);
    }

    public static LootrChestItemRenderer getInstance() {
        return INSTANCE;
    }
}
