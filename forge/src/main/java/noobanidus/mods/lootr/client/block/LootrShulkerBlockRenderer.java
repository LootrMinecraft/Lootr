package noobanidus.mods.lootr.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.blocks.entities.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.config.LootrModConfig;

import java.util.UUID;

public class LootrShulkerBlockRenderer implements BlockEntityRenderer<LootrShulkerBlockEntity> {
    private UUID playerId = null;
    public static final Material MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(LootrAPI.MODID, "shulker"));
    public static final Material MATERIAL2 = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(LootrAPI.MODID, "shulker_opened"));

    private final ShulkerModel<?> model;

    public LootrShulkerBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new ShulkerModel<>(context.bakeLayer(ModelLayers.SHULKER));
    }

    protected Material getMaterial(LootrShulkerBlockEntity blockEntity) {
        if (LootrModConfig.isVanillaTextures()) {
            return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
        }
        if (playerId == null) {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return MATERIAL;
            }
            playerId = player.getUUID();
        }
        if (blockEntity.getOpeners().contains(playerId)) {
            return MATERIAL2;
        }
        return MATERIAL;
    }

    @Override
    public void render(LootrShulkerBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
        Direction direction = Direction.UP;
        if (blockEntity.hasLevel()) {
            BlockState state = blockEntity.getLevel().getBlockState(blockEntity.getPosition());
            if (state.getBlock() instanceof ShulkerBoxBlock) {
                direction = state.getValue(ShulkerBoxBlock.FACING);
            }
        }

        Material material = getMaterial(blockEntity);
        matrixStack.pushPose();

        matrixStack.translate(0.5d, 0.5d, 0.5d);
        matrixStack.scale(0.9995f, 0.9995f, 0.9995f);
        matrixStack.mulPose(direction.getRotation());
        matrixStack.scale(1f, -1f, -1f);
        matrixStack.translate(0, -1d, 0);
        ModelPart part = this.model.getLid();
        part.setPos(0f, 24f - blockEntity.getProgress(partialTicks) * 8f, 0f);
        part.yRot = (float) (270 * blockEntity.getProgress(partialTicks) * Math.PI / 180);
        VertexConsumer consumer = material.buffer(buffer, RenderType::entityCutoutNoCull);
        this.model.renderToBuffer(matrixStack, consumer, light, overlay, 1f, 1, 1f, 1f);

        matrixStack.popPose();
    }
}
