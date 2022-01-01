package noobanidus.mods.lootr.block;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.model.data.ModelProperty;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.block.tile.LootrBarrelTileEntity;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.Random;

public class LootrBarrelBlock extends BarrelBlock {
  public static final ModelProperty<Boolean> OPENED = new ModelProperty<>();

  public LootrBarrelBlock(Properties properties) {
    super(properties);
  }

  @Override
  public void onRemove(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (oldState.getBlock() != newState.getBlock() && world instanceof ServerWorld) {
      DataStorage.deleteLootChest((ServerWorld) world, pos);
    }
    super.onRemove(oldState, world, pos, newState, isMoving);
  }

  @Override
  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
    if (player.isShiftKeyDown()) {
      ChestUtil.handleLootSneak(this, world, pos, player);
    } else {
      ChestUtil.handleLootChest(this, world, pos, player);
    }
    return ActionResultType.SUCCESS;
  }

  @Nullable
  @Override
  public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
    return new LootrBarrelTileEntity();
  }

  @Override
  public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof LootrBarrelTileEntity) {
      ((LootrBarrelTileEntity) te).recheckOpen();
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param) {
    super.triggerEvent(state, world, pos, id, param);
    TileEntity tile = world.getBlockEntity(pos);
    return tile != null && tile.triggerEvent(id, param);
  }
}
