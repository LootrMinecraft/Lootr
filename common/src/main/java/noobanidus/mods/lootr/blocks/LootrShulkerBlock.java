package noobanidus.mods.lootr.blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.init.ModItems;
import noobanidus.mods.lootr.tiles.SpecialLootShulkerTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ShulkerSharedHelper;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class LootrShulkerBlock extends ShulkerBoxBlock {
  public LootrShulkerBlock(Properties pProperties) {
    super(DyeColor.YELLOW, pProperties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
  }

  @Override
  public BlockEntity newBlockEntity(BlockGetter p_196283_1_) {
    return new SpecialLootShulkerTile();
  }

  @Override
  public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
    if (pLevel.isClientSide) {
      return InteractionResult.SUCCESS;
    } else if (pPlayer.isSpectator()) {
      return InteractionResult.CONSUME;
    } else {
      BlockEntity tileentity = pLevel.getBlockEntity(pPos);
      if (tileentity instanceof SpecialLootShulkerTile) {
        SpecialLootShulkerTile shulkerboxtileentity = (SpecialLootShulkerTile) tileentity;
        boolean flag;
        if (shulkerboxtileentity.getAnimationStatus() == ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
          Direction direction = pState.getValue(FACING);
          flag = pLevel.noCollision(ShulkerSharedHelper.openBoundingBox(pPos, direction));
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

        return InteractionResult.CONSUME;
      } else {
        return InteractionResult.PASS;
      }
    }
  }

  @Override
  public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
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
      PiglinAi.angerNearbyPiglins(pPlayer, false);
    }
  }

  @Override
  public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
    ResourceLocation resourcelocation = this.getLootTable();
    if (resourcelocation == BuiltInLootTables.EMPTY) {
      return Collections.emptyList();
    } else {
      LootContext lootcontext = pBuilder.withParameter(LootContextParams.BLOCK_STATE, pState).create(LootContextParamSets.BLOCK);
      ServerLevel serverworld = lootcontext.getLevel();
      LootTable loottable = serverworld.getServer().getLootTables().get(resourcelocation);
      return loottable.getRandomItems(lootcontext);
    }
  }

  @Override
  public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
    if (pState.getBlock() != pNewState.getBlock() && pLevel instanceof ServerLevel) {
      NewChestData.deleteLootChest((ServerLevel) pLevel, pPos);
    }

    if (!pState.is(pNewState.getBlock())) {
      BlockEntity tileentity = pLevel.getBlockEntity(pPos);
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
  public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    BlockEntity tileentity = pLevel.getBlockEntity(pPos);
    return tileentity instanceof SpecialLootShulkerTile ? Shapes.create(((SpecialLootShulkerTile) tileentity).getBoundingBox(pState)) : Shapes.block();
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
    return 0;
  }

  @Override
  public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
    return new ItemStack(ModItems.SHULKER);
  }

  @Override
  @Nullable
  public DyeColor getColor() {
    return DyeColor.YELLOW;
  }
}
