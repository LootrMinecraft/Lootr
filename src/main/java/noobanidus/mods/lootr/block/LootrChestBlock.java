package noobanidus.mods.lootr.block;

import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.tile.LootrChestTileEntity;
import noobanidus.mods.lootr.block.tile.LootrInventoryTileEntity;
import noobanidus.mods.lootr.block.tile.TrappedLootrChestTileEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.util.ChestUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Supplier;

@SuppressWarnings("NullableProblems")
public class LootrChestBlock extends BlockChest {
  private final boolean isInventory;
  public LootrChestBlock(Type type, boolean isInventory) {
    super(type);
    this.isInventory = isInventory;
    this.setSoundType(SoundType.WOOD);
    if(isInventory)
      setTranslationKey("lootr_inventory");
    else if(type == ModBlocks.TYPE_LOOTR_TRAP)
      setTranslationKey("lootr_trapped_chest");
    else
      setTranslationKey("lootr_chest");
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
    else if(chestType == ModBlocks.TYPE_LOOTR_TRAP)
      return new TrappedLootrChestTileEntity();
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
  public boolean canProvidePower(IBlockState state) {
    return chestType == ModBlocks.TYPE_LOOTR_TRAP;
  }

  @Override
  public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
  {
    if (!blockState.canProvidePower())
    {
      return 0;
    }
    else
    {
      return MathHelper.clamp(LootrChestTileEntity.getPlayersUsing(blockAccess, pos), 0, 15);
    }
  }

  @Override
  public CreativeTabs getCreativeTab() {
    return Lootr.TAB;
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return Item.getItemFromBlock(chestType == ModBlocks.TYPE_LOOTR_TRAP ? Blocks.TRAPPED_CHEST : Blocks.CHEST);
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
    if(ConfigManager.BLAST_IMMUNE)
      return Float.MAX_VALUE;
    else if(ConfigManager.BLAST_RESISTANT)
      return 16.0f;
    else
      return super.getExplosionResistance(world, pos, exploder, explosion);
  }

  @Override
  public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
    if(ConfigManager.ZERO_COMPARATOR)
      return 0;
    else
      return 1;
  }
}
