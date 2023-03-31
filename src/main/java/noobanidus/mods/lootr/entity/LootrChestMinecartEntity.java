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
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LootrChestMinecartEntity extends ContainerMinecartEntity implements ILootCart {
  private final Set<UUID> openers = new HashSet<>();
  private boolean opened = false;

  public LootrChestMinecartEntity(EntityType<LootrChestMinecartEntity> type, World world) {
    super(type, world);
  }

  public LootrChestMinecartEntity(World worldIn, double x, double y, double z) {
    super(ModEntities.LOOTR_MINECART_ENTITY, x, y, z, worldIn);
  }

  @Override
  public Set<UUID> getOpeners() {
    return openers;
  }

  public void addOpener(PlayerEntity player) {
    openers.add(player.getUUID());
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
  public boolean isInvulnerableTo(DamageSource source) {
    if (this.isInvulnerable() && source != DamageSource.OUT_OF_WORLD) {
      return true;
    }

    if (source.getEntity() instanceof FakePlayer) {
      return false;
    }

    if (source.getEntity() instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) source.getEntity();
      if (((ConfigManager.DISABLE_BREAK.get() && player.isCreative()) || !ConfigManager.DISABLE_BREAK.get()) && source.getEntity().isShiftKeyDown()) {
        return false;
      } else {
        source.getEntity().sendMessage(new TranslationTextComponent("lootr.message.cart_should_sneak").setStyle(Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.AQUA))), Util.NIL_UUID);
        source.getEntity().sendMessage(new TranslationTextComponent("lootr.message.should_sneak2", new TranslationTextComponent("lootr.message.cart_should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.AQUA))), Util.NIL_UUID);
      }
    } else {
      return true;
    }

    return true;
  }

  @Override
  public void destroy(DamageSource source) {
    this.remove();
    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
      ItemStack itemstack = new ItemStack(Items.MINECART);
      ItemStack itemstack2 = new ItemStack(Items.CHEST);
      if (this.hasCustomName()) {
        itemstack.setHoverName(this.getCustomName());
        itemstack2.setHoverName(this.getCustomName());
      }

      this.spawnAtLocation(itemstack);
      this.spawnAtLocation(itemstack2);
    }
  }


  @Override
  public int getContainerSize() {
    return 27;
  }

  @Override
  public AbstractMinecartEntity.Type getMinecartType() {
    return AbstractMinecartEntity.Type.CHEST;
  }

  private static final BlockState cartNormal = ModBlocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);

  @Override
  public BlockState getDefaultDisplayBlockState() {
    return cartNormal;
  }

  @Override
  public int getDefaultDisplayOffset() {
    return 8;
  }

  @Override
  public Container createMenu(int id, PlayerInventory playerInventoryIn) {
    return ChestContainer.threeRows(id, playerInventoryIn, this);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void remove(boolean keepData) {
    this.removed = true;
    if (!keepData)
      this.invalidateCaps();
  }

  @Override
  protected void addAdditionalSaveData(CompoundNBT compound) {
    if (this.lootTable != null) {
      compound.putString("LootTable", this.lootTable.toString());
    }
    compound.putLong("LootTableSeed", this.lootTableSeed);
    ListNBT list = new ListNBT();
    for (UUID opener : this.openers) {
      list.add(NBTUtil.createUUID(opener));
    }
    compound.put("LootrOpeners", list);
    super.addAdditionalSaveData(compound);
  }

  @Override
  protected void readAdditionalSaveData(CompoundNBT compound) {
    this.lootTable = new ResourceLocation(compound.getString("LootTable"));
    this.lootTableSeed = compound.getLong("LootTableSeed");
    if (compound.contains("LootrOpeners", Constants.NBT.TAG_LIST)) {
      ListNBT openers = compound.getList("LootrOpeners", Constants.NBT.TAG_INT_ARRAY);
      this.openers.clear();
      for (INBT item : openers) {
        this.openers.add(NBTUtil.loadUUID(item));
      }
    }
    super.readAdditionalSaveData(compound);
  }

  @Override
  public ActionResultType interact(PlayerEntity player, Hand hand) {
    ActionResultType ret = ActionResultType.PASS;
    if (ret.consumesAction()) return ret;
    if (player.isShiftKeyDown()) {
      ChestUtil.handleLootCartSneak(player.level, this, player);
      if (!player.level.isClientSide) {
        return ActionResultType.CONSUME;
      } else {
        return ActionResultType.SUCCESS;
      }
    } else {
      ChestUtil.handleLootCart(player.level, this, player);
      if (!player.level.isClientSide) {
        PiglinTasks.angerNearbyPiglins(player, true);
        return ActionResultType.CONSUME;
      } else {
        return ActionResultType.SUCCESS;
      }
    }
  }

  public void addLoot(@Nullable PlayerEntity player, IInventory inventory, @Nullable ResourceLocation overrideTable, long seed) {
    if (this.lootTable != null && this.level.getServer() != null) {
      LootTable loottable = this.level.getServer().getLootTables().get(overrideTable != null ? overrideTable : this.lootTable);
      if (loottable == LootTable.EMPTY) {
        Lootr.LOG.error("Unable to fill loot cart in " + level.dimension() + " at " + position() + " as the loot table '" + (overrideTable != null ? overrideTable : this.lootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
        if (ConfigManager.REPORT_UNRESOLVED_TABLES.get() && player != null) {
          player.sendMessage(new TranslationTextComponent("lootr.message.invalid_table", (overrideTable != null ? overrideTable : this.lootTable).toString()).setStyle(Style.EMPTY.withColor(TextFormatting.DARK_RED).withBold(true)), Util.NIL_UUID);
        }
      }
      if (player instanceof ServerPlayerEntity) {
        CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayerEntity) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld) this.level)).withParameter(LootParameters.ORIGIN, this.position()).withOptionalRandomSeed(ConfigManager.RANDOMISE_SEED.get() ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.lootTableSeed : seed);
      lootcontext$builder.withParameter(LootParameters.KILLER_ENTITY, this);
      if (player != null) {
        lootcontext$builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
      }

      loottable.fill(inventory, lootcontext$builder.create(LootParameterSets.CHEST));
    }
  }

  @Override
  public IPacket<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void startOpen(PlayerEntity player) {
    if (!player.isSpectator()) {
      OpenCart cart = new OpenCart(this.getId());
      PacketHandler.sendToInternal(cart, (ServerPlayerEntity) player);
    }
  }

  @Override
  public void stopOpen(PlayerEntity player) {
    if (!player.isSpectator()) {
      addOpener(player);
    }
  }
}
