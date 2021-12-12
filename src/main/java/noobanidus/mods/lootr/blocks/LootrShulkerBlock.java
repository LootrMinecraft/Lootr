package noobanidus.mods.lootr.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.init.ModItems;
import noobanidus.mods.lootr.tiles.SpecialLootShulkerTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class LootrShulkerBlock extends ShulkerBoxBlock {
  public LootrShulkerBlock(AbstractBlock.Properties pProperties) {
    super(DyeColor.YELLOW, pProperties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
  }

  @Override
  public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
    return new SpecialLootShulkerTile();
  }

  @Override
  public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit) {
    if (pLevel.isClientSide) {
      return ActionResultType.SUCCESS;
    } else if (pPlayer.isSpectator()) {
      return ActionResultType.CONSUME;
    } else {
      TileEntity tileentity = pLevel.getBlockEntity(pPos);
      if (tileentity instanceof SpecialLootShulkerTile) {
        SpecialLootShulkerTile shulkerboxtileentity = (SpecialLootShulkerTile) tileentity;
        boolean flag;
        if (shulkerboxtileentity.getAnimationStatus() == ShulkerBoxTileEntity.AnimationStatus.CLOSED) {
          Direction direction = pState.getValue(FACING);
          flag = pLevel.noCollision(ShulkerAABBHelper.openBoundingBox(pPos, direction));
        } else {
          flag = true;
        }

        if (flag) {
          if (pPlayer.isShiftKeyDown()) {
            ChestUtil.handleLootSneak(this, pLevel, pPos, pPlayer);
          } else {
            ChestUtil.handleLootChest(this, pLevel, pPos, pPlayer);
          }
        }

        return ActionResultType.CONSUME;
      } else {
        return ActionResultType.PASS;
      }
    }
  }

  @Override
  public void playerWillDestroy(World pLevel, BlockPos pPos, BlockState pState, PlayerEntity pPlayer) {
/*    TileEntity tileentity = pLevel.getBlockEntity(pPos);
    if (tileentity instanceof SpecialLootShulkerTile) {
      if (!pLevel.isClientSide) {
        ItemEntity itementity = new ItemEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, new ItemStack(Items.SHULKER_BOX));
        itementity.setDefaultPickUpDelay();
        pLevel.addFreshEntity(itementity);
      }
    }*/

    pLevel.levelEvent(pPlayer, 2001, pPos, getId(pState));
    if (this.is(BlockTags.GUARDED_BY_PIGLINS)) {
      PiglinTasks.angerNearbyPiglins(pPlayer, false);
    }
  }

  @Override
  public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
    ResourceLocation resourcelocation = this.getLootTable();
    if (resourcelocation == LootTables.EMPTY) {
      return Collections.emptyList();
    } else {
      LootContext lootcontext = pBuilder.withParameter(LootParameters.BLOCK_STATE, pState).create(LootParameterSets.BLOCK);
      ServerWorld serverworld = lootcontext.getLevel();
      LootTable loottable = serverworld.getServer().getLootTables().get(resourcelocation);
      return loottable.getRandomItems(lootcontext);
    }
  }

  @Override
  public void onRemove(BlockState pState, World pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
    if (pState.getBlock() != pNewState.getBlock() && pLevel instanceof ServerWorld) {
      NewChestData.deleteLootChest((ServerWorld) pLevel, pPos);
    }

    if (!pState.is(pNewState.getBlock())) {
      TileEntity tileentity = pLevel.getBlockEntity(pPos);
      if (tileentity instanceof SpecialLootShulkerTile) {
        pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
      }

      if (pState.hasTileEntity() && (!pState.is(pNewState.getBlock()) || !pNewState.hasTileEntity())) {
        pLevel.removeBlockEntity(pPos);
      }
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack pStack, @Nullable IBlockReader pLevel, List<ITextComponent> pTooltip, ITooltipFlag pFlag) {
  }

  @Override
  public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
    TileEntity tileentity = pLevel.getBlockEntity(pPos);
    return tileentity instanceof SpecialLootShulkerTile ? VoxelShapes.create(((SpecialLootShulkerTile) tileentity).getBoundingBox(pState)) : VoxelShapes.block();
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, World pLevel, BlockPos pPos) {
    return 0;
  }

  @Override
  public ItemStack getCloneItemStack(IBlockReader pLevel, BlockPos pPos, BlockState pState) {
    return new ItemStack(ModItems.SHULKER);
  }

  @Override
  @Nullable
  public DyeColor getColor() {
    return DyeColor.YELLOW;
  }
}
