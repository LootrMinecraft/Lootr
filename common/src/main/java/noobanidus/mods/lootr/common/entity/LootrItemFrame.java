package noobanidus.mods.lootr.common.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.*;
import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import noobanidus.mods.lootr.common.api.data.SimpleLootrEntityInstance;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinItemFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class LootrItemFrame extends ItemFrame implements ILootrEntity {
  private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
  private final SimpleLootrEntityInstance instance = new SimpleLootrEntityInstance(this, this::getVisualOpeners, 1);

  public LootrItemFrame(EntityType<? extends ItemFrame> entityType, Level level) {
    super(entityType, level);
  }

  public LootrItemFrame(Level level, BlockPos pos, Direction facingDirection) {
    super(LootrRegistry.getItemFrame(), level, pos, facingDirection);
  }

  public void lootrSetItem(ItemStack stack) {
    this.inventory.set(0, stack);
    if (!level().isClientSide()) {
      this.setItemInternal(stack);
    }
  }

  @Override
  public void startSeenByPlayer(ServerPlayer pPlayer) {
    super.startSeenByPlayer(pPlayer);
    // It is possible that these packets will be fired
    // before the client has actually received the initial
    // packet to create the entity, thus resulting in the
    // resolved entity being null.

    if (hasVisualOpened(pPlayer)) {
      performOpen(pPlayer);
    } else {
      performClose(pPlayer);
    }
  }

  @Override
  public boolean survives() {
    // Determine based on Lootr config
    if (!this.level().noCollision(this)) {
      return false;
    } else {
      BlockState blockState = this.level().getBlockState(this.pos.relative(this.direction.getOpposite()));
      return (blockState.isSolid() || this.direction.getAxis()
          .isHorizontal() && DiodeBlock.isDiode(blockState)) && this.level()
          .getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
    }
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
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak")
                .setStyle(LootrAPI.getChatStyle()), false);
            return true;
          } else {
            return false;
          }
        } else {
          player.displayClientMessage(Component.translatable("lootr.message.cannot_break")
              .setStyle(LootrAPI.getChatStyle()), false);
          return true;
        }
      } else if (!source.getEntity().isShiftKeyDown()) {
        ((Player) source.getEntity()).displayClientMessage(Component.translatable("lootr.message.cart_should_sneak")
            .setStyle(LootrAPI.getChatStyle()), false);
        ((Player) source.getEntity()).displayClientMessage(Component.translatable("lootr.message.cart_should_sneak2")
            .setStyle(LootrAPI.getChatStyle()), false);
        return true;
      } else //noinspection RedundantIfStatement
        if (source.getEntity().isShiftKeyDown()) {
          return false;
        }
    } else {
      return true;
    }

    return true;
  }

  @Override
  public boolean hurt(DamageSource source, float amount) {
    // TODO: Depend on config
    if (source.getEntity() instanceof ServerPlayer player) {
      this.actuallyDropItem(player);
    }

    if (this.isInvulnerableTo(source)) {
      return false;
    }

    if (!this.isRemoved() && !this.level().isClientSide) {
      this.kill();
      this.markHurt();
    }

    return true;
  }

  @Override
  public void dropItem(@Nullable Entity entity) {
  }

  @Override
  public void setItem(ItemStack stack, boolean updateNeighbours) {
  }

  private void actuallyDropItem(ServerPlayer player) {
    if (this.level().isClientSide()) {
      return;
    }
    ILootrInventory inventory = LootrAPI.getInventory(this, player);
    if (inventory == null) {
      return;
    }
    if (inventory.getItem(0).isEmpty()) {
      return;
    }
    if (!hasServerOpened(player)) {
      player.awardStat(LootrRegistry.getLootedStat());
      LootrRegistry.getStatTrigger().trigger(player);
    }
    this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);

    inventory.setItem(0, ItemStack.EMPTY);
    inventory.setChanged();
    ItemStack item = getItem().copy();
    this.spawnAtLocation(item);
    this.performTrigger(player);
    if (this.addOpener(player)) {
      this.performOpen(player);
    }
    this.performUpdate(player);
  }

  @Override
  public ItemStack getItem() {
    return this.getEntityData().get(AccessorMixinItemFrame.lootr$getDataItem());
  }

  @Override
  public boolean hasFramedMap() {
    return false;
  }

  @Override
  public SlotAccess getSlot(int slot) {
    return SlotAccess.NULL;
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compound) {
    super.addAdditionalSaveData(compound);
    compound.put(NBTConstants.CUSTOM_INVENTORY, ContainerHelper.saveAllItems(new CompoundTag(), this.inventory, level().registryAccess()));
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compound) {
    super.readAdditionalSaveData(compound);
    ContainerHelper.loadAllItems(compound.getCompound(NBTConstants.CUSTOM_INVENTORY), this.inventory, level().registryAccess());
    this.setItemInternal(this.inventory.getFirst());
  }

  private void setItemInternal(ItemStack stack) {
    if (stack.is(Items.FILLED_MAP)) {
      LootrAPI.LOG.error("ItemFrames with maps are not supported by Lootr Item Frames due to technical limitations.");
      return;
    }

    if (!stack.isEmpty()) {
      stack = stack.copyWithCount(1);
    }

    ((AccessorMixinItemFrame) this).lootr$onItemChanged(stack);
    this.getEntityData().set(AccessorMixinItemFrame.lootr$getDataItem(), stack);
  }

  @Override
  public InteractionResult interact(Player player, InteractionHand hand) {
    if (!this.level().isClientSide) {
      this.playSound(this.getRotateItemSound(), 1.0F, 1.0F);
      this.setRotation(this.getRotation() + 1);
      this.gameEvent(GameEvent.BLOCK_CHANGE, player);

      return InteractionResult.CONSUME;
    } else {
      return InteractionResult.SUCCESS;
    }
  }

  @Override
  public int getAnalogOutput() {
    return 0;
  }

  // TODO:
  @Override
  public ItemStack getPickResult() {
    return new ItemStack(Items.ITEM_FRAME);
  }

  @Override
  protected ItemStack getFrameItemStack() {
    return new ItemStack(Items.ITEM_FRAME);
  }

  @Override
  public @Nullable Set<UUID> getClientOpeners() {
    return instance.getClientOpeners();
  }

  @Override
  public boolean isClientOpened() {
    return instance.isClientOpened();
  }

  @Override
  public void setClientOpened(boolean opened) {
    instance.setClientOpened(opened);
  }

  @Override
  public void markChanged() {
    markDataChanged();
  }

  @Override
  @Deprecated
  public LootrBlockType getInfoBlockType() {
    return null;
  }

  @Override
  public ILootrType getInfoNewType() {
    return BuiltInLootrTypes.ITEM_FRAME;
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return getUUID();
  }

  @Override
  public String getInfoKey() {
    return instance.getInfoKey();
  }

  @Override
  public boolean hasBeenOpened() {
    return instance.hasBeenOpened();
  }

  @Override
  public boolean isPhysicallyOpen() {
    return false;
  }

  @Override
  public @NotNull BlockPos getInfoPos() {
    return BlockPos.containing(position());
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
    return 1;
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
    return inventory;
  }

  @Override
  public boolean isInfoReferenceInventory() {
    return true;
  }

  @Override
  public @Nullable ResourceKey<LootTable> getInfoLootTable() {
    return LootrAPI.ITEM_FRAME_EMPTY;
  }

  @Override
  public long getInfoLootSeed() {
    return 0;
  }

  @AutoService(ILootrEntityConverter.class)
  public static class DefaultConverter implements ILootrEntityConverter<LootrItemFrame> {
    @Override
    public ILootrEntity apply(LootrItemFrame entity) {
      return entity;
    }

    @Override
    public EntityType<?> getEntityType() {
      return LootrRegistry.getItemFrame();
    }
  }
}

