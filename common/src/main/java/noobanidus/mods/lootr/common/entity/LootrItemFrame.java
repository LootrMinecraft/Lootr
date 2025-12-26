package noobanidus.mods.lootr.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class LootrItemFrame extends ItemFrame {
  public LootrItemFrame(EntityType<? extends ItemFrame> entityType, Level level) {
    super(entityType, level);
  }

  public LootrItemFrame(Level level, BlockPos pos, Direction facingDirection) {
    super(EntityType.ITEM_FRAME, level, pos, facingDirection);
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
  public void kill() {
    // TODO:
    this.removeFramedMap(this.getItem());
    super.kill();
  }

  @Override
  public boolean hurt(DamageSource source, float amount) {
    // TODO: Depend on config
    if (this.isInvulnerableTo(source)) {
      return false;
    } else if (!source.is(DamageTypeTags.IS_EXPLOSION) && !this.getItem().isEmpty()) {
      if (!this.level().isClientSide) {
        this.dropItem(source.getEntity(), false);
        this.gameEvent(GameEvent.BLOCK_CHANGE, source.getEntity());
        this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);
      }

      return true;
    } else {
      return super.hurt(source, amount);
    }
  }

  public SoundEvent getRemoveItemSound() {
    return SoundEvents.ITEM_FRAME_REMOVE_ITEM;
  }

  @Override
  public void dropItem(@Nullable Entity entity) {
    this.playSound(this.getBreakSound(), 1.0F, 1.0F);
    this.dropItem(entity, true);
    this.gameEvent(GameEvent.BLOCK_CHANGE, entity);
  }

  private void dropItem(@Nullable Entity entity, boolean dropSelf) {
    ItemStack itemStack = this.getItem();
    this.setItem(ItemStack.EMPTY);
    if (!this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
      if (entity == null) {
        this.removeFramedMap(itemStack);
      }
    } else if (entity instanceof Player player && player.hasInfiniteMaterials()) {
      this.removeFramedMap(itemStack);
    } else {
      if (dropSelf) {
        this.spawnAtLocation(this.getFrameItemStack());
      }

      if (!itemStack.isEmpty()) {
        itemStack = itemStack.copy();
        this.removeFramedMap(itemStack);
        this.spawnAtLocation(itemStack);
      }
    }
  }

  private void removeFramedMap(ItemStack stack) {
    MapId mapId = this.getFramedMapId(stack);
    if (mapId != null) {
      MapItemSavedData mapItemSavedData = MapItem.getSavedData(mapId, this.level());
      if (mapItemSavedData != null) {
        mapItemSavedData.removedFromFrame(this.pos, this.getId());
        mapItemSavedData.setDirty(true);
      }
    }

    stack.setEntityRepresentation(null);
  }

  public ItemStack getItem() {
    // TODO:
    return ItemStack.EMPTY;
  }

  @Nullable
  public MapId getFramedMapId(ItemStack stack) {
    return stack.get(DataComponents.MAP_ID);
  }

  public boolean hasFramedMap() {
    return this.getItem().has(DataComponents.MAP_ID);
  }

  @Override
  public SlotAccess getSlot(int slot) {
    return SlotAccess.NULL;
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compound) {
    super.addAdditionalSaveData(compound);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compound) {
    super.readAdditionalSaveData(compound);
  }

  @Override
  public InteractionResult interact(Player player, InteractionHand hand) {
    ItemStack itemStack = player.getItemInHand(hand);
    boolean bl = !this.getItem().isEmpty();
    boolean bl2 = !itemStack.isEmpty();
    if (!this.level().isClientSide) {
      if (!bl) {
        if (bl2 && !this.isRemoved()) {
          if (itemStack.is(Items.FILLED_MAP)) {
            MapItemSavedData mapItemSavedData = MapItem.getSavedData(itemStack, this.level());
            if (mapItemSavedData != null && mapItemSavedData.isTrackedCountOverLimit(256)) {
              return InteractionResult.FAIL;
            }
          }

          this.setItem(itemStack);
          this.gameEvent(GameEvent.BLOCK_CHANGE, player);
          itemStack.consume(1, player);
        }
      } else {
        this.playSound(this.getRotateItemSound(), 1.0F, 1.0F);
        this.setRotation(this.getRotation() + 1);
        this.gameEvent(GameEvent.BLOCK_CHANGE, player);
      }

      return InteractionResult.CONSUME;
    } else {
      return !bl && !bl2 ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }
  }

  public int getAnalogOutput() {
    return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
  }

  // TODO:
  @Override
  public ItemStack getPickResult() {
    return ItemStack.EMPTY;
  }

  protected ItemStack getFrameItemStack() {
    return new ItemStack(Items.ITEM_FRAME);
  }
}

