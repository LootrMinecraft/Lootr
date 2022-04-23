package noobanidus.mods.lootr.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noobanidus.mods.lootr.Lootr;

public class TrophyBlock extends Block {
  public TrophyBlock() {
    super(Material.IRON);
    this.setSoundType(SoundType.METAL);
    setRegistryName(Lootr.MODID, "trophy");
    setHarvestLevel("pickaxe", 0);
    setTranslationKey("lootr_trophy");
  }

  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    return this.getDefaultState().withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(BlockHorizontal.FACING).getHorizontalIndex();
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.HORIZONTALS[meta]);
  }

  @Override
  protected BlockStateContainer createBlockState()
  {
    return new BlockStateContainer(this, BlockHorizontal.FACING);
  }

  private static final AxisAlignedBB EAST_WEST = new AxisAlignedBB(1.5/16.0, 0, 4.0/16.0d, 14.5/16.0, 14.5/16.0, 12.0/16.0);
  private static final AxisAlignedBB NORTH_SOUTH = new AxisAlignedBB(4.0/16.0, 0, 1.5/16.0, 12.0/16.0, 14.5/16.0, 14.5/16.0);

  @Override
  @SuppressWarnings("deprecation")
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    EnumFacing facing = state.getValue(BlockHorizontal.FACING);
    if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
      return EAST_WEST;
    } else {
      return NORTH_SOUTH;
    }
  }

  @Override
  public CreativeTabs getCreativeTab() {
    return Lootr.TAB;
  }
}
