package noobanidus.mods.lootr.block;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.tile.LootrShulkerTileEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModItems;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class LootrShulkerBlock extends BlockShulkerBox {
  public LootrShulkerBlock() {
    super(EnumDyeColor.YELLOW);
    setTranslationKey("lootr_shulker");
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta)
  {
    return new LootrShulkerTileEntity();
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
  {
    if (worldIn.isRemote)
    {
      return true;
    }
    else if (playerIn.isSpectator())
    {
      return true;
    }
    else
    {
      TileEntity tileentity = worldIn.getTileEntity(pos);

      if (tileentity instanceof LootrShulkerTileEntity)
      {
        EnumFacing enumfacing = state.getValue(FACING);
        boolean flag;

        if (((LootrShulkerTileEntity)tileentity).getAnimationStatus() == TileEntityShulkerBox.AnimationStatus.CLOSED)
        {
          AxisAlignedBB axisalignedbb = FULL_BLOCK_AABB.expand(0.5F * (float)enumfacing.getXOffset(), 0.5F * (float)enumfacing.getYOffset(), 0.5F * (float)enumfacing.getZOffset()).contract(enumfacing.getXOffset(), enumfacing.getYOffset(), enumfacing.getZOffset());
          flag = !worldIn.collidesWithAnyBlock(axisalignedbb.offset(pos.offset(enumfacing)));
        }
        else
        {
          flag = true;
        }

        if (flag)
        {
          if (playerIn.isSneaking()) {
            ChestUtil.handleLootSneak(this, worldIn, pos, playerIn);
          } else {
            ChestUtil.handleLootChest(this, worldIn, pos, playerIn);
          }
        }

        return true;
      }
      else
      {
        return false;
      }
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
  {
    
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
  {
    TileEntity tileentity = source.getTileEntity(pos);
    return tileentity instanceof LootrShulkerTileEntity ? ((LootrShulkerTileEntity)tileentity).getBoundingBox(state) : FULL_BLOCK_AABB;
  }

  @Override
  public boolean hasComparatorInputOverride(IBlockState pState) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(IBlockState pBlockState, World pLevel, BlockPos pPos) {
    if(ConfigManager.ZERO_COMPARATOR)
      return 0;
    else
      return 1;
  }

  @Override
  public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
    return new ItemStack(ModItems.SHULKER);
  }

  @Override
  @Nullable
  public EnumDyeColor getColor() {
    return EnumDyeColor.YELLOW;
  }

  @Override
  public CreativeTabs getCreativeTab() {
    return Lootr.TAB;
  }

  @Override
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
    TileEntity tileentity = worldIn.getTileEntity(pos);

    if (tileentity instanceof LootrShulkerTileEntity)
    {
      LootrShulkerTileEntity tileentityshulkerbox = (LootrShulkerTileEntity)tileentity;

      ItemStack itemstack = new ItemStack(Item.getItemFromBlock(Blocks.PURPLE_SHULKER_BOX));
      if (tileentityshulkerbox.hasCustomName())
      {
        itemstack.setStackDisplayName(tileentityshulkerbox.getName());
        tileentityshulkerbox.setCustomName("");
      }
      spawnAsEntity(worldIn, pos, itemstack);
      worldIn.updateComparatorOutputLevel(pos, state.getBlock());
    }
    super.breakBlock(worldIn, pos, state);
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, @org.jetbrains.annotations.Nullable Entity exploder, Explosion explosion) {
    if(ConfigManager.BLAST_RESISTANT)
      return 16.0f;
    else
      return super.getExplosionResistance(world, pos, exploder, explosion);
  }
}
