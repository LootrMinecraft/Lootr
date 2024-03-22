package noobanidus.mods.lootr.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.entity.ILootCart;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.network.OpenCart;
import noobanidus.mods.lootr.network.PacketHandler;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LootrChestMinecartEntity extends AbstractMinecartContainer implements ILootCart {
  private final Set<UUID> openers = new HashSet<>();
  private boolean opened = false;

  public LootrChestMinecartEntity(EntityType<LootrChestMinecartEntity> type, Level world) {
    super(type, world);
  }

  public LootrChestMinecartEntity(Level worldIn, double x, double y, double z) {
    super(ModEntities.LOOTR_MINECART_ENTITY.get(), x, y, z, worldIn);
  }

  @Override
  public void unpackChestVehicleLootTable(@org.jetbrains.annotations.Nullable Player p_219950_) {
  }

  @Override
  public Set<UUID> getOpeners() {
    return openers;
  }

  public void addOpener(Player player) {
    openers.add(player.getUUID());
    setChanged();
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
    if (this.isInvulnerable() && source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
      return true;
    }

    if (source.getEntity() instanceof Player player) {
      if ((player instanceof FakePlayer && ConfigManager.ENABLE_FAKE_PLAYER_BREAK.get()) || ConfigManager.ENABLE_BREAK.get()) {
        return false;
      }
      if (ConfigManager.DISABLE_BREAK.get()) {
        if (player.getAbilities().instabuild) {
          if (!player.isShiftKeyDown()) {
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), false);
            return true;
          } else {
            return false;
          }
        } else {
          player.displayClientMessage(Component.translatable("lootr.message.cannot_break").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), false);
          return true;
        }
      } else if (!source.getEntity().isShiftKeyDown()) {
        ((Player) source.getEntity()).displayClientMessage(Component.translatable("lootr.message.cart_should_sneak").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), false);
        ((Player) source.getEntity()).displayClientMessage(Component.translatable("lootr.message.should_sneak2", Component.translatable("lootr.message.cart_should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))), false);
      }
    } else {
      return true;
    }

    return true;
  }

  @Override
  public void destroy(DamageSource source) {
    this.remove(Entity.RemovalReason.KILLED);
    if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
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
  public AbstractMinecart.Type getMinecartType() {
    return AbstractMinecart.Type.CHEST;
  }

  private static BlockState cartNormal = null;

  @Override
  public BlockState getDefaultDisplayBlockState() {
    if (cartNormal == null) {
      cartNormal = ModBlocks.CHEST.get().defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
    }
    return cartNormal;
  }

  @Override
  public int getDefaultDisplayOffset() {
    return 8;
  }

  @Override
  public AbstractContainerMenu createMenu(int id, Inventory playerInventoryIn) {
    return ChestMenu.threeRows(id, playerInventoryIn, this);
  }

  @Override
  public void remove(RemovalReason reason) {
    this.setRemoved(reason);
    if (reason == Entity.RemovalReason.KILLED) {
      this.gameEvent(GameEvent.ENTITY_DIE);
    }
    this.invalidateCaps();
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag compound) {
    if (this.lootTable != null) {
      compound.putString("LootTable", this.lootTable.toString());
    }
    compound.putLong("LootTableSeed", this.lootTableSeed);
    ListTag list = new ListTag();
    for (UUID opener : this.openers) {
      list.add(NbtUtils.createUUID(opener));
    }
    compound.put("LootrOpeners", list);
    super.addAdditionalSaveData(compound);
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag compound) {
    this.lootTable = new ResourceLocation(compound.getString("LootTable"));
    this.lootTableSeed = compound.getLong("LootTableSeed");
    if (compound.contains("LootrOpeners", Tag.TAG_LIST)) {
      ListTag openers = compound.getList("LootrOpeners", Tag.TAG_INT_ARRAY);
      this.openers.clear();
      for (Tag item : openers) {
        this.openers.add(NbtUtils.loadUUID(item));
      }
    }
    super.readAdditionalSaveData(compound);
  }

  @Override
  public InteractionResult interact(Player player, InteractionHand hand) {
    InteractionResult ret = InteractionResult.PASS;
    if (ret.consumesAction()) return ret;
    if (player.isShiftKeyDown()) {
      ChestUtil.handleLootCartSneak(player.level(), this, player);
      if (!player.level().isClientSide) {
        return InteractionResult.CONSUME;
      } else {
        return InteractionResult.SUCCESS;
      }
    } else {
      ChestUtil.handleLootCart(player.level(), this, player);
      if (!player.level().isClientSide) {
        PiglinAi.angerNearbyPiglins(player, true);
        return InteractionResult.CONSUME;
      } else {
        return InteractionResult.SUCCESS;
      }
    }
  }

  public void addLoot(@Nullable Player player, Container inventory, @Nullable ResourceLocation overrideTable, long seed) {
    if (this.lootTable != null && this.level().getServer() != null) {
      LootTable loottable = this.level().getServer().getLootData().getLootTable(overrideTable != null ? overrideTable : this.lootTable);
      if (loottable == LootTable.EMPTY) {
        LootrAPI.LOG.error("Unable to fill loot in " + level().dimension() + " at " + position() + " as the loot table '" + (overrideTable != null ? overrideTable : this.lootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
        if (ConfigManager.REPORT_UNRESOLVED_TABLES.get() && player != null) {
          player.displayClientMessage(Component.translatable("lootr.message.invalid_table", (overrideTable != null ? overrideTable : this.lootTable).toString()).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)).withBold(true)), false);
        }
      }
      if (player instanceof ServerPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      LootParams.Builder builder = (new LootParams.Builder((ServerLevel)this.level())).withParameter(LootContextParams.ORIGIN, position());
      builder.withParameter(LootContextParams.KILLER_ENTITY, this);
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
      }

      loottable.fill(inventory, builder.create(LootContextParamSets.CHEST), LootrAPI.getLootSeed(seed == Long.MIN_VALUE ? this.lootTableSeed : seed));
    }
  }

  @Override
  public Packet<ClientGamePacketListener> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void startOpen(Player player) {
    if (!player.isSpectator()) {
      OpenCart cart = new OpenCart(this.getId());
      PacketHandler.sendToInternal(cart, (ServerPlayer) player);
    }
  }

  @Override
  public void stopOpen(Player player) {
    if (!player.isSpectator()) {
      addOpener(player);
    }
  }

  @Override
  public void startSeenByPlayer(ServerPlayer pPlayer) {
    super.startSeenByPlayer(pPlayer);

    if (getOpeners().contains(pPlayer.getUUID())) {
      OpenCart cart = new OpenCart(getId());
      PacketHandler.sendToInternal(cart, pPlayer);
    }
  }

  @Override
  public Item getDropItem() {
    return Items.CHEST_MINECART;
  }
}
