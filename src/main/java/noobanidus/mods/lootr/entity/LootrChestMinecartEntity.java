package noobanidus.mods.lootr.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.util.*;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.networking.OpenCart;
import noobanidus.mods.lootr.networking.PacketHandler;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LootrChestMinecartEntity extends ContainerMinecartEntity {
  private List<UUID> openers = new ArrayList<>();
  private boolean opened = false;

  public LootrChestMinecartEntity(EntityType<LootrChestMinecartEntity> type, World world) {
    super(type, world);
  }

  public LootrChestMinecartEntity(World worldIn, double x, double y, double z) {
    super(ModEntities.LOOTR_MINECART_ENTITY, x, y, z, worldIn);
  }

  public List<UUID> getOpeners() {
    return openers;
  }

  public void addOpener(PlayerEntity player) {
    openers.add(player.getUniqueID());
  }

  public boolean isOpened() {
    return opened;
  }

  public void setOpened() {
    this.opened = true;
  }

  @Override
  public boolean isInvulnerableTo(DamageSource source) {
    if (this.isInvulnerable() && source != DamageSource.OUT_OF_WORLD && !source.isCreativePlayer()) {
      return true;
    }

    if (source.getTrueSource() instanceof PlayerEntity) {
      if (source.getTrueSource().isSneaking()) {
        return false;
      } else {
        source.getTrueSource().sendMessage(new TranslationTextComponent("lootr.message.cart_should_sneak").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.AQUA))), Util.DUMMY_UUID);
        source.getTrueSource().sendMessage(new TranslationTextComponent("lootr.message.should_sneak2", new TranslationTextComponent("lootr.message.cart_should_sneak3").setStyle(Style.EMPTY.setBold(true))).setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.AQUA))), Util.DUMMY_UUID);
      }
    } else {
      return true;
    }

    return true;
  }

  @Override
  public void killMinecart(DamageSource source) {
    this.remove();
    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
      ItemStack itemstack = new ItemStack(Items.MINECART);
      ItemStack itemstack2 = new ItemStack(Items.CHEST);
      if (this.hasCustomName()) {
        itemstack.setDisplayName(this.getCustomName());
        itemstack2.setDisplayName(this.getCustomName());
      }

      this.entityDropItem(itemstack);
      this.entityDropItem(itemstack2);
    }
  }


  @Override
  public int getSizeInventory() {
    return 27;
  }

  @Override
  public AbstractMinecartEntity.Type getMinecartType() {
    return AbstractMinecartEntity.Type.CHEST;
  }

  private static BlockState cartNormal = ModBlocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.NORTH);

  @Override
  public BlockState getDefaultDisplayTile() {
    return cartNormal;
  }

  @Override
  public int getDefaultDisplayTileOffset() {
    return 8;
  }

  @Override
  public Container createContainer(int id, PlayerInventory playerInventoryIn) {
    return ChestContainer.createGeneric9X3(id, playerInventoryIn, this);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void remove(boolean keepData) {
    this.removed = true;
    if (!keepData)
      this.invalidateCaps();
  }

  @Override
  protected void writeAdditional(CompoundNBT compound) {
    if (this.lootTable != null) {
      compound.putString("LootTable", this.lootTable.toString());
    }
    compound.putLong("LootTableSeed", this.lootTableSeed);
    ListNBT list = new ListNBT();
    for (UUID opener : this.openers) {
      list.add(NBTUtil.func_240626_a_(opener));
    }
    compound.put("LootrOpeners", list);
    super.writeAdditional(compound);
  }

  @Override
  protected void readAdditional(CompoundNBT compound) {
    this.lootTable = new ResourceLocation(compound.getString("LootTable"));
    this.lootTableSeed = compound.getLong("LootTableSeed");
    if (compound.contains("LootrOpeners", Constants.NBT.TAG_LIST)) {
      ListNBT openers = compound.getList("LootrOpeners", Constants.NBT.TAG_INT_ARRAY);
      this.openers.clear();
      for (INBT item : openers) {
        this.openers.add(NBTUtil.readUniqueId(item));
      }
    }
    super.readAdditional(compound);
  }

  @Override
  public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
    ActionResultType ret = ActionResultType.PASS;
    if (ret.isSuccessOrConsume()) return ret;
    ChestUtil.handleLootCart(player.world, this, player);
    if (!player.world.isRemote) {
      PiglinTasks.func_234478_a_(player, true);
      return ActionResultType.CONSUME;
    } else {
      return ActionResultType.SUCCESS;
    }
  }

  public void addLoot(@Nullable PlayerEntity player, IInventory inventory) {
    if (this.lootTable != null && this.world.getServer() != null) {
      LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
      if (player instanceof ServerPlayerEntity) {
        CriteriaTriggers.PLAYER_GENERATES_CONTAINER_LOOT.test((ServerPlayerEntity) player, this.lootTable);
      }

      LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld) this.world)).withParameter(LootParameters.field_237457_g_, this.getPositionVec()).withSeed(this.lootTableSeed);
      lootcontext$builder.withParameter(LootParameters.KILLER_ENTITY, this);
      if (player != null) {
        lootcontext$builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
      }

      loottable.fillInventory(inventory, lootcontext$builder.build(LootParameterSets.CHEST));
    }
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void openInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
      OpenCart cart = new OpenCart(this.getEntityId());
      PacketHandler.sendToInternal(cart, (ServerPlayerEntity) player);
    }
  }

  @Override
  public void closeInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
      addOpener(player);
    }
  }
}
