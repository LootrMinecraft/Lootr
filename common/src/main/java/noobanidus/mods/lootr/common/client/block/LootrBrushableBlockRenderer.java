package noobanidus.mods.lootr.common.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import noobanidus.mods.lootr.common.block.entity.LootrBrushableBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;

public class LootrBrushableBlockRenderer implements BlockEntityRenderer<LootrBrushableBlockEntity> {
  private final ItemRenderer itemRenderer;

  public LootrBrushableBlockRenderer(BlockEntityRendererProvider.Context arg) {
    this.itemRenderer = arg.getItemRenderer();
  }

  public void render(LootrBrushableBlockEntity arg, float f, PoseStack arg2, MultiBufferSource arg3, int k, int l) {
    if (arg.getLevel() != null) {
      Player player = ClientHooks.getPlayer();
      if (player == null) {
        return;
      }
      if (arg.hasOpened(player)) {
        return;
      }
      if (!arg.isBrushingPlayer(player)) {
        return;
      }

      int i = arg.getBlockState().getValue(BlockStateProperties.DUSTED);
      if (i > 0) {
        Direction direction = arg.getHitDirection();
        if (direction != null) {
          ItemStack itemstack = arg.getItem();
          if (!itemstack.isEmpty()) {
            arg2.pushPose();
            arg2.translate(0.0F, 0.5F, 0.0F);
            float[] afloat = this.translations(direction, i);
            arg2.translate(afloat[0], afloat[1], afloat[2]);
            arg2.mulPose(Axis.YP.rotationDegrees(75.0F));
            boolean flag = direction == Direction.EAST || direction == Direction.WEST;
            arg2.mulPose(Axis.YP.rotationDegrees((float) ((flag ? 90 : 0) + 11)));
            arg2.scale(0.5F, 0.5F, 0.5F);
            int j = LevelRenderer.getLightColor(arg.getLevel(), arg.getBlockState(), arg.getBlockPos()
                .relative(direction));
            this.itemRenderer.renderStatic(itemstack, ItemDisplayContext.FIXED, j, OverlayTexture.NO_OVERLAY, arg2, arg3, arg.getLevel(), 0);
            arg2.popPose();
          }
        }
      }
    }
  }

  private float[] translations(Direction arg, int i) {
    float[] afloat = new float[]{0.5F, 0.0F, 0.5F};
    float f = (float) i / 10.0F * 0.75F;
    switch (arg) {
      case EAST:
        afloat[0] = 0.73F + f;
        break;
      case WEST:
        afloat[0] = 0.25F - f;
        break;
      case UP:
        afloat[1] = 0.25F + f;
        break;
      case DOWN:
        afloat[1] = -0.23F - f;
        break;
      case NORTH:
        afloat[2] = 0.25F - f;
        break;
      case SOUTH:
        afloat[2] = 0.73F + f;
    }

    return afloat;
  }

  // Where did this come from?
  public AABB getRenderBoundingBox(LootrBrushableBlockEntity blockEntity) {
    BlockPos pos = blockEntity.getBlockPos();
    return new AABB(
        (double) pos.getX() - 0.25,
        (double) pos.getY() - 0.25,
        (double) pos.getZ() - 0.25,
        (double) pos.getX() + 1.25,
        (double) pos.getY() + 1.25,
        (double) pos.getZ() + 1.25
    );
  }
}