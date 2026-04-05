package noobanidus.mods.lootr.common.entity;

import com.google.auto.service.AutoService;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
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
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.interfaces.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.data.IKeyedData;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import noobanidus.mods.lootr.common.api.interfaces.wrapper.ILootrEntityWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.UUID;

public class LootrChestMinecartEntity extends AbstractMinecartContainer implements ILootrEntity {
  private static BlockState cartNormal = null;
  // This can actually just be a null
  private final Set<UUID> clientOpeners = new ObjectLinkedOpenHashSet<>();
  // TODO: This isn't synchronized properly
  private boolean hasBeenOpened = false;
  // This is only ever set via packet
  private boolean opened = false;
  private int cachedKey;
  private Identifier cachedIdentifier;

  public LootrChestMinecartEntity(EntityType<LootrChestMinecartEntity> type, Level world) {
    super(type, world);
  }

  public LootrChestMinecartEntity(Level worldIn, double x, double y, double z) {
    super(LootrRegistry.getMinecart(), worldIn);
    setInitialPos(x, y, z);
  }

  @Override
  public @NonNull ItemStack getPickResult() {
    return new ItemStack(Items.CHEST_MINECART);
  }

  @Override
  public void unpackChestVehicleLootTable(@Nullable Player p_219950_) {
  }

  @Override
  public @Nullable Set<UUID> getClientOpeners() {
    return clientOpeners;
  }

  @Override
  public boolean isClientOpened() {
    return opened;
  }

  @Override
  public void setClientOpened(boolean opened) {
    this.opened = opened;
  }

  @Override
  public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float f) {
    if (isInvulnerableTo(damageSource)) {
      return false;
    }

    return super.hurtServer(serverLevel, damageSource, f);
  }

  // TODO: Abstract this out into SimpleLootrEntity
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
            player.sendSystemMessage(Component.translatable("lootr.message.cannot_break_sneak")
                .setStyle(LootrAPI.getChatStyle()));
            return true;
          } else {
            return false;
          }
        } else {
          player.sendSystemMessage(Component.translatable("lootr.message.cannot_break")
              .setStyle(LootrAPI.getChatStyle()));
          return true;
        }
      } else if (!source.getEntity().isShiftKeyDown()) {
        ((Player) source.getEntity()).sendSystemMessage(Component.translatable("lootr.message.cart_should_sneak")
            .setStyle(LootrAPI.getChatStyle()));
        ((Player) source.getEntity()).sendSystemMessage(Component.translatable("lootr.message.cart_should_sneak2")
            .setStyle(LootrAPI.getChatStyle()));
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
  public void tick() {
    super.tick();
    if (!this.level().isClientSide()) {
      LootrAPI.handleInstanceTick(this);
    } else {
      LootrAPI.handleInstanceClientTick(this);
    }
  }


  @Override
  public int getContainerSize() {
    return 27;
  }

  @Override
  public @NonNull BlockState getDefaultDisplayBlockState() {
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
  public @NonNull AbstractContainerMenu createMenu(int id, @NonNull Inventory playerInventoryIn) {
    return ChestMenu.threeRows(id, playerInventoryIn, this);
  }

  @Override
  public void remove(@NonNull RemovalReason reason) {
    this.setRemoved(reason);
    if (reason == Entity.RemovalReason.KILLED) {
      this.gameEvent(GameEvent.ENTITY_DIE);
    }
  }


  @Override
  public @NonNull InteractionResult interact(@NonNull Player player, @NonNull InteractionHand hand, @NonNull Vec3 location) {
    if (level().isClientSide() || player.isSpectator() || !(player instanceof ServerPlayer serverPlayer)) {
      return InteractionResult.CONSUME;
    }

    if (player.isShiftKeyDown()) {
      LootrAPI.handleInstanceSneak(this, serverPlayer);
    } else {
      LootrAPI.handleInstanceOpen(this, serverPlayer);
    }
    return InteractionResult.SUCCESS;
  }


  @Override
  public void startOpen(@NonNull ContainerUser user) {
    if (user instanceof ServerPlayer player) {
      if (!hasBeenOpened) {
        hasBeenOpened = true;
        markInstanceChanged();
      }
      performOpen(player);
    }
  }

  @Override
  public void startSeenByPlayer(@NonNull ServerPlayer pPlayer) {
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
  public @NotNull BlockPos getDataPos() {
    return blockPosition();
  }

  @Override
  public ResourceKey<LootTable> getDataLootTable() {
    return getContainerLootTable();
  }

  @Override
  public @Nullable Component getDataDisplayName() {
    return getDisplayName();
  }

  @Override
  public @NotNull ResourceKey<Level> getDataDimension() {
    return level().dimension();
  }

  @Override
  public int getDataContainerSize() {
    return getContainerSize();
  }

  @Override
  public long getDataLootSeed() {
    return getContainerLootTableSeed();
  }

  // TODO:
  @Override
  public @Nullable NonNullList<ItemStack> getDataReferenceInventory() {
    return null;
  }

  @Override
  public boolean isDataReferenceInventory() {
    return false;
  }

  @Override
  public Level getDataLevel() {
    return level();
  }

  @Override
  public @NonNull ILootrType getDataType() {
    return BuiltInLootrTypes.MINECART;
  }

  @Override
  public @NotNull Vec3 getDataVec() {
    return position();
  }

  @Override
  @NotNull
  public UUID getDataId() {
    return getUUID();
  }

  private boolean cacheChecked = false;

  @Override
  public int getDataKey() {
    if (!cacheChecked) {
      cacheChecked = true;
      this.cachedKey = IKeyedData.generateInfoIntKey(getDataId());
    }
    return cachedKey;
  }

  @Override
  public Identifier getDataIdentifier() {
    if (this.cachedIdentifier == null) {
      this.cachedIdentifier = IKeyedData.generateInfoIdentifier(getDataId());
    }
    return cachedIdentifier;
  }

  @Override
  public boolean hasBeenOpened() {
    return hasBeenOpened;
  }

  @Override
  public boolean isPhysicallyOpen() {
    return false;
  }

  @Override
  public void markInstanceChanged() {
    setChanged();
    markSectionChanged();
  }

  @Override
  public double getParticleYOffset() {
    return 1.1;
  }

  @Override
  public double[] getParticleXBounds() {
    return new double[]{0.3, 0.7};
  }

  @Override
  public double[] getParticleZBounds() {
    return new double[]{0.3, 0.7};
  }

  @Override
  public @Nullable IContainerTrigger getTrigger() {
    return LootrRegistry.getCartTrigger();
  }

  @Override
  protected @NonNull Vec3 applyNaturalSlowdown(@NonNull Vec3 incoming) {
    float f = 0.98F;
    if (this.isInWater()) {
      f *= 0.95F;
    }

    return incoming.multiply(f, 0, f);
  }

  @Override
  protected @NonNull Item getDropItem() {
    return Items.CHEST_MINECART;
  }

  @AutoService(ILootrEntityWrapper.class)
  public static class DefaultWrapper implements ILootrEntityWrapper<LootrChestMinecartEntity> {
    @Override
    public ILootrEntity apply(LootrChestMinecartEntity entity) {
      return entity;
    }

    @Override
    public EntityType<?> getEntityType() {
      return LootrRegistry.getMinecart();
    }
  }
}
