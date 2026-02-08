package noobanidus.mods.lootr.common.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.type.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.wrapper.ILootrEntityWrapper;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
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
    stack = stack.copyWithCount(1);
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
    if (LootrAPI.canItemFramesSelfSupport()) {
      return true;
    }
    // Determine based on Lootr config
    if (!this.level().noCollision(this)) {
      return false;
    } else {
      BlockState blockstate = this.level().getBlockState(this.pos.relative(this.getDirection().getOpposite()));
      return blockstate.isSolid() || this.getDirection().getAxis()
          .isHorizontal() && DiodeBlock.isDiode(blockstate) ? this.canCoexist(true) : false;
    }
  }

  @Override
  public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
    boolean skipMessage = false;

    if (amount > 0 && source.getEntity() instanceof ServerPlayer player) {
      if (this.actuallyDropItem(player)) {
        skipMessage = true;
      }
    }

    if (amount > 0 && !skipMessage) {
      maybeMessagePlayer(source);
    }

    if (this.isInvulnerableTo(source)) {
      return false;
    }

    if (!this.isRemoved() && !this.level().isClientSide()) {
      this.kill((ServerLevel) this.level());
      this.markHurt();
    }

    return true;
  }

  public boolean isInvulnerableTo(DamageSource source) {
    // This is called multiple times so it can't be relied upon for messaging
    if (this.isInvulnerable() && source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
      return true;
    }

    if (source.getEntity() instanceof Player player) {
      if (LootrAPI.canDestroyOrBreak(player)) {
        return false;
      }
      if (LootrAPI.isBreakDisabled()) {
        if (player.getAbilities().instabuild) {
          return !player.isShiftKeyDown();
        } else {
          return true;
        }
      } else if (!source.getEntity().isShiftKeyDown()) {
        return true;
      } else //noinspection RedundantIfStatement
        if (source.getEntity().isShiftKeyDown()) {
          return false;
        }
    }

    return true;
  }

  private void maybeMessagePlayer(DamageSource source) {
    if (source.getEntity() instanceof Player player && !this.level().isClientSide()) {
      if (LootrAPI.canDestroyOrBreak(player)) {
        return;
      }
      if (LootrAPI.isBreakDisabled()) {
        if (player.getAbilities().instabuild) {
          if (!player.isShiftKeyDown()) {
            player.displayClientMessage(Component.translatable("lootr.message.cannot_break_sneak")
                .setStyle(LootrAPI.getChatStyle()), false);
          }
        } else {
          player.displayClientMessage(Component.translatable("lootr.message.cannot_break")
              .setStyle(LootrAPI.getChatStyle()), false);
        }
      } else if (!source.getEntity().isShiftKeyDown()) {
        ((Player) source.getEntity()).displayClientMessage(Component.translatable("lootr.message.cart_should_sneak")
            .setStyle(LootrAPI.getChatStyle()), false);
        ((Player) source.getEntity()).displayClientMessage(Component.translatable("lootr.message.cart_should_sneak2")
            .setStyle(LootrAPI.getChatStyle()), false);
      }
    }
  }

  @Override
  public void setItem(ItemStack stack, boolean updateNeighbours) {
  }

  private boolean actuallyDropItem(ServerPlayer player) {
    if (this.level().isClientSide()) {
      return false;
    }
    ILootrInventory inventory = LootrAPI.getInventory(this, player);
    if (inventory == null) {
      return false;
    }
    if (inventory.getItem(0).isEmpty()) {
      return false;
    }
    if (!hasServerOpened(player)) {
      player.awardStat(LootrRegistry.getLootedStat());
      LootrRegistry.getStatTrigger().trigger(player);
    }
    this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);

    inventory.setItem(0, ItemStack.EMPTY);
    inventory.setChanged();
    ItemStack item = getItem().copy();
    this.spawnAtLocation((ServerLevel) this.level(), item);
    this.performTrigger(player);
    if (this.addOpener(player)) {
      this.performOpen(player);
    }
    this.performUpdate(player);
    return true;
  }

  @Override
  public ItemStack getItem() {
    return this.getEntityData().get(AccessorMixinItemFrame.lootr$getDataItem());
  }

  @Override
  public @Nullable IContainerTrigger getTrigger() {
    return LootrRegistry.getItemFrameTrigger();
  }

  @Override
  public boolean hasFramedMap() {
    return false;
  }

  private static final SlotAccess NULL = new SlotAccess() {
    @Override
    public ItemStack get() {
      return ItemStack.EMPTY;
    }

    @Override
    public boolean set(ItemStack stack) {
      return false;
    }
  };

  @Override
  public SlotAccess getSlot(int slot) {
    return NULL;
  }

  @Override
  public void addAdditionalSaveData(ValueOutput output) {
    super.addAdditionalSaveData(output);
    ContainerHelper.saveAllItems(output, this.inventory, true);
  }

  @Override
  public void readAdditionalSaveData(ValueInput input) {
    super.readAdditionalSaveData(input);
    ContainerHelper.loadAllItems(input, this.inventory);
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
  public void tick() {
    super.tick();
    if (!this.level().isClientSide()) {
      LootrAPI.handleProviderTick(this);
    } else {
      LootrAPI.handleProviderClientTick(this);
    }
  }

  @Override
  public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
    if (!this.level().isClientSide()) {
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
  public ILootrType getInfoType() {
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

  @Override
  public Level getInfoLevel() {
    return level();
  }

  @Override
  public Vec3 getParticleCenter() {
    return this.position();
    //return Vec3.atCenterOf(BlockPos.containing(this.position()));
  }

  @Override
  public double getParticleYOffset() {
    return 0.4f;
  }

  @Override
  public double[] getParticleXBounds() {
    if (this.getDirection().getAxis() == Direction.Axis.Z) {
      return new double[]{-0.35, 0.35};
    } else {
      if (this.getDirection() == Direction.SOUTH || this.getDirection() == Direction.EAST) {
        return new double[]{0.05, 0.1};
      } else {
        return new double[]{-0.1, -0.05};
      }
    }
  }

  @Override
  public double[] getParticleZBounds() {
    if (this.getDirection().getAxis() == Direction.Axis.X) {
      return new double[]{-0.35, 0.35};
    } else {
      if (this.getDirection() == Direction.SOUTH || this.getDirection() == Direction.EAST) {
        return new double[]{0.05, 0.1};
      } else {
        return new double[]{-0.1, -0.05};
      }
    }
  }

  @AutoService(ILootrEntityWrapper.class)
  public static class DefaultWrapper implements ILootrEntityWrapper<LootrItemFrame> {
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

