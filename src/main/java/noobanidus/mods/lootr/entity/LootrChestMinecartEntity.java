package noobanidus.mods.lootr.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.*;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.entity.ILootCart;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.networking.OpenCart;
import noobanidus.mods.lootr.networking.PacketHandler;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LootrChestMinecartEntity extends EntityMinecartContainer implements ILootCart {
  private final Set<UUID> openers = new HashSet<>();
  private boolean opened = false;

  public LootrChestMinecartEntity(World worldIn) {
    super(worldIn);
  }

  public LootrChestMinecartEntity(World worldIn, double x, double y, double z) {
    super(worldIn);
    setPosition(x, y, z);
  }

  @Override
  public Set<UUID> getOpeners() {
    return openers;
  }

  public void addOpener(EntityPlayer player) {
    openers.add(player.getUniqueID());
  }

  public boolean isOpened() {
    return opened;
  }

  public void setOpened() {
    this.opened = true;
  }

  public void setClosed() {
    this.opened = false;
  }

  @Override
  public boolean isEntityInvulnerable(DamageSource source) {
    if (this.getIsInvulnerable() && source != DamageSource.OUT_OF_WORLD && !source.isCreativePlayer()) {
      return true;
    }

    if (source.getTrueSource() instanceof EntityPlayer) {
      if (source.getTrueSource().isSneaking()) {
        return false;
      } else {
        source.getTrueSource().sendMessage(new TextComponentTranslation("lootr.message.cart_should_sneak").setStyle(new Style().setColor(TextFormatting.AQUA)));
        source.getTrueSource().sendMessage(new TextComponentTranslation("lootr.message.should_sneak2", new TextComponentTranslation("lootr.message.cart_should_sneak3").setStyle(new Style().setBold(true))).setStyle(new Style().setColor(TextFormatting.AQUA)));
      }
    } else {
      return true;
    }

    return true;
  }

  @Override
  public void killMinecart(DamageSource source) {
    this.setDead();
    if (this.world.getGameRules().getBoolean("doEntityDrops")) {
      ItemStack itemstack = new ItemStack(Items.MINECART);
      ItemStack itemstack2 = new ItemStack(Item.getItemFromBlock(Blocks.CHEST));
      if (this.hasCustomName()) {
        itemstack.setStackDisplayName(this.getCustomNameTag());
        itemstack2.setStackDisplayName(this.getCustomNameTag());
      }

      this.entityDropItem(itemstack, 0);
      this.entityDropItem(itemstack2, 0);
    }
  }


  @Override
  public int getSizeInventory() {
    return 27;
  }

  @Override
  public EntityMinecart.Type getType() {
    return EntityMinecart.Type.CHEST;
  }

  private static final IBlockState cartNormal = ModBlocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);

  @Override
  public IBlockState getDefaultDisplayTile() {
    return cartNormal;
  }

  @Override
  public int getDefaultDisplayTileOffset() {
    return 8;
  }

  @Override
  public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
  {
    return new ContainerChest(playerInventory, this, playerIn);
  }

  @Override
  protected void writeEntityToNBT(NBTTagCompound compound) {
    if (this.lootTable != null) {
      compound.setString("LootTable", this.lootTable.toString());
    }
    compound.setLong("LootTableSeed", this.lootTableSeed);
    NBTTagList list = new NBTTagList();
    for (UUID opener : this.openers) {
      list.appendTag(NBTUtil.createUUIDTag(opener));
    }
    compound.setTag("LootrOpeners", list);
    super.writeEntityToNBT(compound);
  }

  @Override
  protected void readEntityFromNBT(NBTTagCompound compound) {
    this.lootTable = new ResourceLocation(compound.getString("LootTable"));
    this.lootTableSeed = compound.getLong("LootTableSeed");
    if (compound.hasKey("LootrOpeners", Constants.NBT.TAG_LIST)) {
      NBTTagList openers = compound.getTagList("LootrOpeners", Constants.NBT.TAG_COMPOUND);
      this.openers.clear();
      for (NBTBase item : openers) {
        this.openers.add(NBTUtil.getUUIDFromTag((NBTTagCompound)item));
      }
    }
    super.readEntityFromNBT(compound);
  }

  @Override
  public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
    if (super.processInitialInteract(player, hand)) return true;
    if (player.isSneaking()) {
      ChestUtil.handleLootCartSneak(player.world, this, player);
      return true;
    } else {
      ChestUtil.handleLootCart(player.world, this, player);
      return true;
    }
  }

  public void addLoot(@Nullable EntityPlayer player, IInventory inventory, @Nullable ResourceLocation overrideTable, long seed) {
    if (this.lootTable != null && this.world.getMinecraftServer() != null) {
      LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(overrideTable != null ? overrideTable : this.lootTable);
      if (loottable == LootTable.EMPTY_LOOT_TABLE) {
        Lootr.LOG.error("Unable to fill loot cart in " + this.world + " at " + this.getPosition() + " as the loot table '" + (overrideTable != null ? overrideTable : this.lootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
      }

      Random random;
      long theSeed = Lootr.CONFIG_RANDOMIZE_SEED ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.lootTableSeed : seed;
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
  public void openInventory(EntityPlayer player) {
    if (!player.isSpectator()) {
      OpenCart cart = new OpenCart(this.getEntityId());
      PacketHandler.sendToInternal(cart, (EntityPlayerMP) player);
    }
  }

  @Override
  public void closeInventory(EntityPlayer player) {
    if (!player.isSpectator()) {
      addOpener(player);
    }
  }

  public String getGuiID()
  {
    return "minecraft:chest";
  }
}
