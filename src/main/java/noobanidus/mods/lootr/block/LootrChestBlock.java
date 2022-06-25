package noobanidus.mods.lootr.block;

import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.tile.LootrChestTileEntity;
import noobanidus.mods.lootr.block.tile.LootrInventoryTileEntity;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@SuppressWarnings("NullableProblems")
public class LootrChestBlock extends BlockChest {
  private final boolean isInventory;
  public LootrChestBlock(Type type, boolean isInventory) {
    super(type);
    this.isInventory = isInventory;
    this.setSoundType(SoundType.WOOD);
    setTranslationKey(isInventory ? "lootr_inventory" : "lootr_chest");
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (player.isSneaking()) {
      ChestUtil.handleLootSneak(this, world, pos, player);
    } else if (!this.isBlocked(world, pos)) {
      if(isInventory)
        ChestUtil.handleLootInventory(this, world, pos, player);
      else
        ChestUtil.handleLootChest(this, world, pos, player);
    }
    return true;
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    if(isInventory)
      return new LootrInventoryTileEntity();
    else
      return new LootrChestTileEntity();
  }

  @Override
  @SuppressWarnings("deprecated")
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return NOT_CONNECTED_AABB;
  }

  @Override
  public IBlockState checkForSurroundingChests(World world, BlockPos pos, IBlockState state) {
    return state;
  }

  @Override
  public CreativeTabs getCreativeTab() {
    return Lootr.TAB;
  }
}
