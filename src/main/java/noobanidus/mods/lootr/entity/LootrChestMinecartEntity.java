package noobanidus.mods.lootr.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.network.to_client.PacketOpenCart;
import noobanidus.mods.lootr.util.ChestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LootrChestMinecartEntity extends AbstractMinecartContainer implements ILootrCart {
  private static BlockState cartNormal = null;
  private final Set<UUID> openers = new HashSet<>();
  private final Set<UUID> actualOpeners = new HashSet<>();
  private boolean opened = false;

  public LootrChestMinecartEntity(EntityType<LootrChestMinecartEntity> type, Level world) {
    super(type, world);
  }

  public LootrChestMinecartEntity(Level worldIn, double x, double y, double z) {
    super(LootrRegistry.getMinecart(), x, y, z, worldIn);
  }

  @Override
  public void unpackChestVehicleLootTable(@Nullable Player p_219950_) {
  }

  @Override
  public Set<UUID> getVisualOpeners() {
    return openers;
  }

  @Override
  public Set<UUID> getActualOpeners() {
    return actualOpeners;
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
      if (LootrAPI.canDestroyOrBreak(player)) {
        return false;
      }
      if (LootrAPI.isBreakDisabled()) {
        if (player.getAbilities().instabuild) {
          if (!player.isShiftKeyDown()) {
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak").setStyle(LootrAPI.getChatStyle()), false);
            return true;
          } else {
            return false;
          }
        } else {
          player.displayClientMessage(Component.translatable("lootr.message.cannot_break").setStyle(LootrAPI.getChatStyle()), false);
          return true;
        }
      } else if (!source.getEntity().isShiftKeyDown()) {
        ((Player) source.getEntity()).displayClientMessage(Component.translatable("lootr.message.cart_should_sneak").setStyle(LootrAPI.getChatStyle()), false);
        ((Player) source.getEntity()).displayClientMessage(Component.translatable("lootr.message.should_sneak2", Component.translatable("lootr.message.cart_should_sneak3").setStyle(Style.EMPTY.withBold(true))).setStyle(LootrAPI.getChatStyle()), false);
        // TODO: I think this is broken
      }
    } else {
      return true;
    }

    return true;
  }

  @Override
  public Item getDropItem() {
    return Items.CHEST_MINECART;
  }

  @Override
  public int getContainerSize() {
    return 27;
  }

  @Override
  public AbstractMinecart.Type getMinecartType() {
    return AbstractMinecart.Type.CHEST;
  }

  @Override
  public BlockState getDefaultDisplayBlockState() {
    if (cartNormal == null) {
      cartNormal = LootrRegistry.getChestBlock().defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
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
    // TODO Neo: still needed?
    //this.invalidateCaps();
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag compound) {
    ListTag list = new ListTag();
    for (UUID opener : this.openers) {
      list.add(NbtUtils.createUUID(opener));
    }
    compound.put("LootrOpeners", list);
    ListTag list2 = new ListTag();
    for (UUID opener : this.actualOpeners) {
      list2.add(NbtUtils.createUUID(opener));
    }
    compound.put("LootrActualOpeners", list2);
    super.addAdditionalSaveData(compound);
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag compound) {
    if (compound.contains("LootrOpeners", Tag.TAG_LIST)) {
      ListTag openers = compound.getList("LootrOpeners", Tag.TAG_INT_ARRAY);
      this.openers.clear();
      for (Tag item : openers) {
        this.openers.add(NbtUtils.loadUUID(item));
      }
    }
    if (compound.contains("LootrActualOpeners", Tag.TAG_LIST)) {
      ListTag openers = compound.getList("LootrActualOpeners", Tag.TAG_INT_ARRAY);
      this.actualOpeners.clear();
      for (Tag item : openers) {
        this.actualOpeners.add(NbtUtils.loadUUID(item));
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

  @Override
  public void startOpen(Player player) {
    if (!player.isSpectator()) {
      PacketDistributor.sendToPlayer((ServerPlayer) player, new PacketOpenCart(this.getId()));
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

    if (getVisualOpeners().contains(pPlayer.getUUID())) {
      PacketDistributor.sendToPlayer((ServerPlayer) pPlayer, new PacketOpenCart(this.getId()));
    }
  }

  @Override
  public @NotNull BlockPos getInfoPos() {
    return blockPosition();
  }

  @Override
  public ResourceKey<LootTable> getInfoLootTable() {
    return getLootTable();
  }

  @Override
  public @Nullable Component getInfoDisplayName() {
    return getDisplayName();
  }

  @Override
  public @NotNull ResourceKey<Level> getInfoDimension() {
    return level().dimension();
  }

  @Override
  public int getInfoContainerSize() {
    return getContainerSize();
  }

  @Override
  public long getInfoLootSeed() {
    return getLootTableSeed();
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
    return null;
  }

  @Override
  public boolean isInfoReferenceInventory() {
    return false;
  }

  @Override
  public Level getInfoLevel() {
    return level();
  }

  @Override
  public @NotNull Vec3 getInfoVec() {
    return position();
  }

  @Override
  @NotNull
  public UUID getInfoUUID() {
    return getUUID();
  }
}
