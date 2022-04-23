package noobanidus.mods.lootr.block.tile;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModTiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LootrShulkerTileEntity extends TileEntityLockableLoot implements ILootTile, ITickable {
  public Set<UUID> openers = new HashSet<>();
  private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
  private int openCount;
  private TileEntityShulkerBox.AnimationStatus animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSED;
  private float progress;
  private float progressOld;
  private ResourceLocation savedLootTable = null;
  private long seed = -1;
  private UUID tileId = null;
  private boolean opened;

  public LootrShulkerTileEntity() {
    super();
  }

  @Override
  public void update() {
    this.updateAnimation();
    if (this.animationStatus == TileEntityShulkerBox.AnimationStatus.OPENING || this.animationStatus == TileEntityShulkerBox.AnimationStatus.CLOSING) {
      this.moveCollidedEntities();
    }
  }

  protected void updateAnimation() {
    this.progressOld = this.progress;
    switch (this.animationStatus) {
      case CLOSED:
        this.progress = 0.0F;
        break;
      case OPENING:
        this.progress += 0.1F;
        if (this.progress >= 1.0F) {
          this.moveCollidedEntities();
          this.animationStatus = TileEntityShulkerBox.AnimationStatus.OPENED;
          this.progress = 1.0F;
        }
        break;
      case CLOSING:
        this.progress -= 0.1F;
        if (this.progress <= 0.0F) {
          this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSED;
          this.progress = 0.0F;
        }
        break;
      case OPENED:
        this.progress = 1.0F;
    }

  }

  public TileEntityShulkerBox.AnimationStatus getAnimationStatus() {
    return this.animationStatus;
  }

  public AxisAlignedBB getBoundingBox(IBlockState p_190584_1_)
  {
    return this.getBoundingBox((EnumFacing)p_190584_1_.getValue(BlockShulkerBox.FACING));
  }

  public AxisAlignedBB getBoundingBox(EnumFacing p_190587_1_)
  {
    return Block.FULL_BLOCK_AABB.expand((double)(0.5F * this.getProgress(1.0F) * (float)p_190587_1_.getXOffset()), (double)(0.5F * this.getProgress(1.0F) * (float)p_190587_1_.getYOffset()), (double)(0.5F * this.getProgress(1.0F) * (float)p_190587_1_.getZOffset()));
  }

  private AxisAlignedBB getTopBoundingBox(EnumFacing p_190588_1_)
  {
    EnumFacing enumfacing = p_190588_1_.getOpposite();
    return this.getBoundingBox(p_190588_1_).contract((double)enumfacing.getXOffset(), (double)enumfacing.getYOffset(), (double)enumfacing.getZOffset());
  }

  private void moveCollidedEntities()
  {
    IBlockState iblockstate = this.world.getBlockState(this.getPos());

    if (iblockstate.getBlock() instanceof BlockShulkerBox)
    {
      EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(BlockShulkerBox.FACING);
      AxisAlignedBB axisalignedbb = this.getTopBoundingBox(enumfacing).offset(this.pos);
      List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);

      if (!list.isEmpty())
      {
        for (int i = 0; i < list.size(); ++i)
        {
          Entity entity = list.get(i);

          if (entity.getPushReaction() != EnumPushReaction.IGNORE)
          {
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 0.0D;
            AxisAlignedBB axisalignedbb1 = entity.getEntityBoundingBox();

            switch (enumfacing.getAxis())
            {
              case X:

                if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                {
                  d0 = axisalignedbb.maxX - axisalignedbb1.minX;
                }
                else
                {
                  d0 = axisalignedbb1.maxX - axisalignedbb.minX;
                }

                d0 = d0 + 0.01D;
                break;
              case Y:

                if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                {
                  d1 = axisalignedbb.maxY - axisalignedbb1.minY;
                }
                else
                {
                  d1 = axisalignedbb1.maxY - axisalignedbb.minY;
                }

                d1 = d1 + 0.01D;
                break;
              case Z:

                if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                {
                  d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
                }
                else
                {
                  d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
                }

                d2 = d2 + 0.01D;
            }

            entity.move(MoverType.SHULKER_BOX, d0 * (double)enumfacing.getXOffset(), d1 * (double)enumfacing.getYOffset(), d2 * (double)enumfacing.getZOffset());
          }
        }
      }
    }
  }

  public int getSizeInventory()
  {
    return 27;
  }

  /**
   * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
   */
  public int getInventoryStackLimit()
  {
    return 64;
  }

  public boolean receiveClientEvent(int id, int type)
  {
    if (id == 1)
    {
      this.openCount = type;

      if (type == 0)
      {
        this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSING;
      }

      if (type == 1)
      {
        this.animationStatus = TileEntityShulkerBox.AnimationStatus.OPENING;
      }

      return true;
    }
    else
    {
      return super.receiveClientEvent(id, type);
    }
  }

  @Override
  public void openInventory(EntityPlayer pPlayer) {
    if (!pPlayer.isSpectator()) {
      if (this.openCount < 0) {
        this.openCount = 0;
      }

      ++this.openCount;
      this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.openCount);
      if (this.openCount == 1) {
        this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      }
    }

  }

  @Override
  public void closeInventory(EntityPlayer pPlayer) {
    if (!pPlayer.isSpectator()) {
      --this.openCount;
      this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.openCount);
      if (this.openCount <= 0) {
        this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      }
      openers.add(pPlayer.getUniqueID());
      updatePacketViaState();
    }

  }

  public String getName()
  {
    return this.hasCustomName() ? this.customName : "container.shulkerBox";
  }

  public String getGuiID()
  {
    return "minecraft:shulker_box";
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    if (compound.hasKey("specialLootChest_table", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("specialLootChest_table"));
    }
    if (compound.hasKey("specialLootChest_seed", Constants.NBT.TAG_LONG)) {
      seed = compound.getLong("specialLootChest_seed");
    }
    if (savedLootTable == null && compound.hasKey("LootTable", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("LootTable"));
      if (compound.hasKey("LootTableSeed", Constants.NBT.TAG_LONG)) {
        seed = compound.getLong("LootTableSeed");
      }
      setLootTable(savedLootTable, seed);
    }
    if (compound.hasUniqueId("tileId")) {
      this.tileId = compound.getUniqueId("tileId");
    } else if (this.tileId == null) {
      getTileId();
    }
    if (compound.hasKey("LootrOpeners")) {
      NBTTagList openers = compound.getTagList("LootrOpeners", Constants.NBT.TAG_COMPOUND);
      this.openers.clear();
      for (NBTBase item : openers) {
        this.openers.add(NBTUtil.getUUIDFromTag((NBTTagCompound)item));
      }
    }
    super.readFromNBT(compound);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound = super.writeToNBT(compound);
    if (savedLootTable != null) {
      compound.setString("specialLootBarrel_table", savedLootTable.toString());
      compound.setString("LootTable", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.setLong("specialLootBarrel_seed", seed);
      compound.setLong("LootTableSeed", seed);
    }
    compound.setUniqueId("tileId", getTileId());
    NBTTagList list = new NBTTagList();
    for (UUID opener : this.openers) {
      list.appendTag(NBTUtil.createUUIDTag(opener));
    }
    compound.setTag("LootrOpeners", list);
    return compound;
  }

  @Override
  public <T> T getCapability(Capability<T> cap, EnumFacing side) {
    return null;
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return this.itemStacks;
  }

  public boolean isEmpty()
  {
    for (ItemStack itemstack : this.itemStacks)
    {
      if (!itemstack.isEmpty())
      {
        return false;
      }
    }

    return true;
  }

  public float getProgress(float pPartialTicks) {
    return this.progressOld + (this.progress - this.progressOld) * pPartialTicks;
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
    super.setLootTable(lootTableIn, seedIn);
  }

  @Nullable
  @SideOnly(Side.CLIENT)
  public EnumDyeColor getColor() {
    return EnumDyeColor.YELLOW;
  }

  @Override
  public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
  {
    return new ContainerShulkerBox(playerInventory, this, playerIn);
  }

  public boolean isClosed() {
    return this.animationStatus == TileEntityShulkerBox.AnimationStatus.CLOSED;
  }

  @Override
  public void fillWithLoot(EntityPlayer player, IInventory inventory, @Nullable ResourceLocation overrideTable, long seed) {
    if (this.world != null && this.savedLootTable != null && this.world.getMinecraftServer() != null) {
      BlockPos worldPosition = this.getPos();
      LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(overrideTable != null ? overrideTable : this.savedLootTable);
      if (loottable == LootTable.EMPTY_LOOT_TABLE) {
        Lootr.LOG.error("Unable to fill loot chest in " + this.world + " at " + worldPosition + " as the loot table '" + (overrideTable != null ? overrideTable : this.savedLootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
      }
      if (player instanceof EntityPlayerMP) {
        //CriteriaTriggers.GENERATE_LOOT.trigger((EntityPlayerMP) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      Random random;
      long theSeed = Lootr.CONFIG_RANDOMIZE_SEED ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.seed : seed;
      LootContext.Builder builder = (new LootContext.Builder((WorldServer) this.world));
      if (player != null) {
        builder.withLuck(player.getLuck()).withPlayer(player);
      }

      if (theSeed == 0L)
      {
        random = new Random();
      }
      else
      {
        random = new Random(theSeed);
      }


      loottable.fillInventory(inventory, random, builder.build());
    }
  }

  @Override
  public ResourceLocation getTable() {
    return savedLootTable;
  }

  @Override
  public Set<UUID> getOpeners() {
    return openers;
  }

  @Override
  public UUID getTileId() {
    if (this.tileId == null) {
      this.tileId = UUID.randomUUID();
    }
    return this.tileId;
  }

  @Override
  public void updatePacketViaState() {
    if (world != null && !world.isRemote) {
      IBlockState state = world.getBlockState(getPos());
      world.notifyBlockUpdate(getPos(), state, state, 8);
    }
  }

  @Override
  @Nonnull
  public NBTTagCompound getUpdateTag() {
    return writeToNBT(new NBTTagCompound());
  }

  @Nullable
  public SPacketUpdateTileEntity getUpdatePacket()
  {
    return new SPacketUpdateTileEntity(this.pos, 10, this.getUpdateTag());
  }

  @Override
  public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) {
    readFromNBT(pkt.getNbtCompound());
    this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
  }

  public boolean isOpened() {
    return opened;
  }

  public void setOpened(boolean opened) {
    this.opened = opened;
  }
}
