package net.zestyblaze.lootr.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.blocks.entities.LootrChestBlockEntity;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.registry.LootrBlockEntityInit;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class LootrChestBlockRenderer<T extends LootrChestBlockEntity & ILootBlockEntity> extends ChestRenderer<T> {
  private UUID playerId = null;
  public static final Material CHEST = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(LootrAPI.MODID, "chest"));
  public static final Material CHEST_OPENED = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(LootrAPI.MODID, "chest_opened"));
  public static final Material OLD_CHEST = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(LootrAPI.MODID, "old_chest"));
  public static final Material OLD_CHEST_OPENED = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(LootrAPI.MODID, "old_chest_opened"));
  public static final Material TRAPPED_CHEST = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(LootrAPI.MODID, "chest_trapped_chest"));
  public static final Material TRAPPED_CHEST_OPENED = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(LootrAPI.MODID, "chest_trapped_opened"));
  public static final Material OLD_TRAPPED_CHEST = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(LootrAPI.MODID, "old_chest_trapped"));
  public static final Material OLD_TRAPPED_CHEST_OPENED = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(LootrAPI.MODID, "old_chest_trapped_opened"));
  private final ModelPart lid;
  private final ModelPart bottom;
  private final ModelPart lock;

  public LootrChestBlockRenderer(BlockEntityRendererProvider.Context context) {
    super(context);
    ModelPart modelPart = context.bakeLayer(ModelLayers.CHEST);
    this.bottom = modelPart.getChild("bottom");
    this.lid = modelPart.getChild("lid");
    this.lock = modelPart.getChild("lock");
  }

  public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    Level level = blockEntity.getLevel();
    BlockState blockState = level != null ? blockEntity.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
    poseStack.pushPose();
    float f = blockState.getValue(ChestBlock.FACING).toYRot();
    poseStack.translate(0.5D, 0.5D, 0.5D);
    poseStack.mulPose(Vector3f.YP.rotationDegrees(-f));
    poseStack.translate(-0.5D, -0.5D, -0.5D);

    float g = blockEntity.getOpenNess(partialTick);
    g = 1.0F - g;
    g = 1.0F - g * g * g;
    Material material = getMaterial(blockEntity);
    VertexConsumer vertexConsumer = material.buffer(bufferSource, RenderType::entityCutout);
    this.render(poseStack, vertexConsumer, this.lid, this.lock, this.bottom, g, packedLight, packedOverlay);

    poseStack.popPose();
  }

  private void render(PoseStack poseStack, VertexConsumer consumer, ModelPart lidPart, ModelPart lockPart, ModelPart bottomPart, float lidAngle, int packedLight, int packedOverlay) {
    lidPart.xRot = -(lidAngle * 1.5707964F);
    lockPart.xRot = lidPart.xRot;
    lidPart.render(poseStack, consumer, packedLight, packedOverlay);
    lockPart.render(poseStack, consumer, packedLight, packedOverlay);
    bottomPart.render(poseStack, consumer, packedLight, packedOverlay);
  }

  protected Material getMaterial(T tile) {
    if (LootrModConfig.isVanillaTextures()) {
      return Sheets.CHEST_LOCATION;
    }
    boolean o = LootrModConfig.isOldTextures();
    boolean t = tile.getType() == LootrBlockEntityInit.SPECIAL_TRAPPED_LOOT_CHEST;
    if(playerId == null) {
      Player player = Minecraft.getInstance().player;
      if(player != null) {
        playerId = player.getUUID();
      } else {
        if (o) {
          if (t) {
            return OLD_TRAPPED_CHEST;
          } else {
            return OLD_CHEST;
          }
        } else {
          if (t) {
            return TRAPPED_CHEST;
          } else {
            return CHEST;
          }
        }
      }
    }
    if(tile.isOpened()) {
      if (o) {
        if (t) {
          return OLD_TRAPPED_CHEST_OPENED;
        } else {
          return OLD_CHEST_OPENED;
        }
      } else {
        if (t) {
          return TRAPPED_CHEST_OPENED;
        } else {
          return CHEST_OPENED;
        }
      }
    }
    if(tile.getOpeners().contains(playerId)) {
      if (o) {
        if (t) {
          return OLD_TRAPPED_CHEST_OPENED;
        } else {
          return OLD_CHEST_OPENED;
        }
      } else {
        if (t) {
          return TRAPPED_CHEST_OPENED;
        } else {
          return CHEST_OPENED;
        }
      }
    } else {
      if (o) {
        if (t) {
          return OLD_TRAPPED_CHEST;
        } else {
          return OLD_CHEST;
        }
      } else {
        if (t) {
          return TRAPPED_CHEST;
        } else {
          return CHEST;
        }
      }
    }
  }
}
