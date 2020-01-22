package noobanidus.mods.lootr.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import noobanidus.mods.lootr.data.BooleanData;
import noobanidus.mods.lootr.data.ChestData;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = ChestBlock.class, priority = 10000)
public abstract class MixinChestBlock {
  private boolean isLootChest(IWorld world, BlockPos pos) {
    return BooleanData.isLootChest(world, pos);
  }

  @Nullable
  private INamedContainerProvider getLootContainer(IWorld world, BlockPos pos, ServerPlayerEntity player) {
    return ChestData.getInventory(world, pos, player);
  }

  @Inject(
      method = "createNewTileEntity",
      at = @At("HEAD"),
      cancellable = true
  )
  private void createNewTileEntity(IBlockReader worldIn, CallbackInfoReturnable<TileEntity> cir) {
    cir.setReturnValue(new SpecialLootChestTile());
    cir.cancel();
  }

  @Inject(
      method = "getDirectionToAttach",
      at = @At("HEAD"),
      cancellable = true
  )
  private void getDirectionToAttach(BlockItemUseContext context, Direction direction, CallbackInfoReturnable<Direction> cir) {
    if (isLootChest(context.getWorld(), context.getPos())) {
      cir.setReturnValue(null);
      cir.cancel();
    }
  }

  @Inject(
      method = "updatePostPlacement",
      at = {@At(value = "RETURN", ordinal = 0),
          @At(value = "RETURN", ordinal = 2)},
      cancellable = true
  )
  private void updatePostPlacement1(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos, CallbackInfoReturnable<BlockState> cir) {
    if (isLootChest(worldIn, currentPos)) {
      cir.setReturnValue(stateIn.with(ChestBlock.TYPE, ChestType.SINGLE));
      cir.cancel();
    }
  }

  @Inject(
      method = "onBlockActivated",
      at = @At("HEAD"),
      cancellable = true
  )
  public void onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit, CallbackInfoReturnable<Boolean> ci) {
    if (!worldIn.isRemote && isLootChest(worldIn, pos)) {
      INamedContainerProvider inamedcontainerprovider = getLootContainer(worldIn, pos, (ServerPlayerEntity) player);
      if (inamedcontainerprovider != null) {
        player.openContainer(inamedcontainerprovider);
        ci.setReturnValue(true);
      } else {
        ci.setReturnValue(false);
      }
      ci.cancel();
    }
  }
}
