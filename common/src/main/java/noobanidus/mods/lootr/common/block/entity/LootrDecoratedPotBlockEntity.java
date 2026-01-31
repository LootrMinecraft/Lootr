package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;
import noobanidus.mods.lootr.common.api.*;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import noobanidus.mods.lootr.common.api.data.SimpleLootrInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class LootrDecoratedPotBlockEntity extends BlockEntity implements RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem, ILootrBlockEntity {
  public long wobbleStartedAtTick;
  @Nullable
  public DecoratedPotBlockEntity.WobbleStyle lastWobbleStyle;
  @Nullable
  private PotDecorationsAdapter decorations;
  @Nullable
  protected ResourceKey<LootTable> lootTable;
  protected long lootTableSeed;

  private final SimpleLootrInstance lootrInstance = new SimpleLootrInstance(this::getVisualOpeners, 1);

  public LootrDecoratedPotBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(LootrRegistry.getDecoratedPotBlockEntity(), blockPos, blockState);
    this.decorations = PotDecorationsAdapter.EMPTY;
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
    super.saveAdditional(compoundTag, provider);
    this.trySaveLootTable(compoundTag);
    this.getDecorations().save(compoundTag);
    this.lootrInstance.saveAdditional(compoundTag, provider, level == null || level.isClientSide());
  }

  @Override
  protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
    super.loadAdditional(compoundTag, provider);
    this.decorations = this.getDecorations().load(compoundTag);
    this.tryLoadLootTable(compoundTag);
    this.lootrInstance.loadAdditional(compoundTag, provider);
  }


  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
    CompoundTag compoundTag = super.getUpdateTag(provider);
    this.saveAdditional(compoundTag, provider);
    this.lootrInstance.fillUpdateTag(compoundTag, provider, level != null && level.isClientSide());
    return compoundTag;
  }

  @Nullable
  public ItemStack popItem(ServerPlayer player) {
    ILootrInventory inventory = LootrAPI.getInventory(this, player);
    if (inventory == null) {
      return null;
    }

    ItemStack result = inventory.getItem(0);
    if (result.isEmpty()) {
      return null;
    }

    inventory.setItem(0, ItemStack.EMPTY);
    inventory.setChanged();

    this.performTrigger(player);
    boolean shouldUpdate = false;
    if (!this.hasServerOpened(player)) {
      player.awardStat(LootrRegistry.getLootedStat());
      LootrRegistry.getStatTrigger().trigger(player);
    }
    if (this.addOpener(player)) {
      this.performOpen(player);
      shouldUpdate = true;
    }
    this.lootrInstance.setHasBeenOpened();

    if (shouldUpdate) {
      this.performUpdate(player);
    }

    return result;
  }

  @Override
  public void performOpen(ServerPlayer player) {
    ILootrBlockEntity.super.performOpen(player);
    PlatformAPI.performPotBreak(this, player);
  }

  public boolean dropContent(ServerPlayer player) {
    if (this.level != null && this.level.getServer() != null) {
      ItemStack theItem = this.popItem(player);
      if (theItem != null) {
        double d = EntityType.ITEM.getWidth();
        double e = 1.0 - d;
        double f = d / 2.0;
        Direction direction = Direction.UP;
        BlockPos blockPos = this.worldPosition.relative(direction, 1);
        double g = (double) blockPos.getX() + 0.5 * e + f;
        double h = (double) blockPos.getY() + 0.5 + (double) (EntityType.ITEM.getHeight() / 2.0F);
        double i = (double) blockPos.getZ() + 0.5 * e + f;
        ItemEntity itemEntity = new ItemEntity(this.level, g, h, i, theItem.split(this.level.random.nextInt(21) + 10));
        itemEntity.setDeltaMovement(Vec3.ZERO);
        this.level.addFreshEntity(itemEntity);

        for (ItemStack item : getDecorations().ordered()) {
          ItemStack sherdStack = item.copy();
          ItemEntity sherdEntity = new ItemEntity(this.level, g, h, i, sherdStack);
          sherdEntity.setDeltaMovement(Vec3.ZERO);
          this.level.addFreshEntity(sherdEntity);
        }

        PlatformAPI.performPotBreak(this, (ServerPlayer) player);
        return true;
      }
    }

    return false;
  }

  public Direction getDirection() {
    return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
  }

  public PotDecorationsAdapter getDecorations() {
    if (this.decorations == null) {
      this.decorations = PotDecorationsAdapter.EMPTY;
    }
    return this.decorations;
  }

  public void setFromItem(ItemStack itemStack) {
    this.applyComponentsFromItemStack(itemStack);
  }

  public ItemStack getPotAsItem() {
    ItemStack itemStack = LootrRegistry.getDecoratedPotItem().getDefaultInstance();
    itemStack.applyComponents(this.collectComponents());
    return itemStack;
  }

  @Nullable
  @Override
  public ResourceKey<LootTable> getLootTable() {
    return this.lootTable;
  }

  @Override
  public void setLootTable(@Nullable ResourceKey<LootTable> resourceKey) {
    this.lootTable = resourceKey;
  }

  @Override
  public long getLootTableSeed() {
    return this.lootTableSeed;
  }

  @Override
  public void setLootTableSeed(long l) {
    this.lootTableSeed = l;
  }

  @Override
  protected void collectImplicitComponents(DataComponentMap.Builder builder) {
    super.collectImplicitComponents(builder);
    if (lootTable != null) {
      builder.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(lootTable, lootTableSeed));
    }
  }

  @Override
  protected void applyImplicitComponents(DataComponentGetter dataComponentInput) {
    super.applyImplicitComponents(dataComponentInput);
    this.decorations = LootrAPI.getDecorationsAdapter(dataComponentInput);
    SeededContainerLoot loot = dataComponentInput.get(DataComponents.CONTAINER_LOOT);
    if (loot != null && loot.lootTable() != null) {
      this.lootTable = loot.lootTable();
      this.lootTableSeed = loot.seed();
    }
  }

  @Override
  public void removeComponentsFromTag(CompoundTag compoundTag) {
    super.removeComponentsFromTag(compoundTag);
    compoundTag.remove("LootTable");
    compoundTag.remove("LootTableSeed");
  }

  @Override
  public ItemStack getTheItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack splitTheItem(int i) {
    return ItemStack.EMPTY;
  }

  @Override
  public void setTheItem(ItemStack itemStack) {
  }

  @Override
  public BlockEntity getContainerBlockEntity() {
    return this;
  }

  public void wobble(DecoratedPotBlockEntity.WobbleStyle wobbleStyle) {
    if (this.level != null && !this.level.isClientSide()) {
      this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, wobbleStyle.ordinal());
    }
  }

  @Override
  public boolean triggerEvent(int i, int j) {
    if (this.level != null && i == 1 && j >= 0 && j < DecoratedPotBlockEntity.WobbleStyle.values().length) {
      this.wobbleStartedAtTick = this.level.getGameTime();
      this.lastWobbleStyle = DecoratedPotBlockEntity.WobbleStyle.values()[j];
      return true;
    } else {
      return super.triggerEvent(i, j);
    }
  }

  @Override
  public @Nullable Set<UUID> getClientOpeners() {
    return lootrInstance.getClientOpeners();
  }

  @Override
  public boolean isClientOpened() {
    return lootrInstance.isClientOpened();
  }

  @Override
  public void setClientOpened(boolean opened) {
    lootrInstance.setClientOpened(opened);
  }

  @Override
  public void markChanged() {
    setChanged();
    markDataChanged();
  }

  @Override
  @Deprecated
  public LootrBlockType getInfoBlockType() {
    return LootrBlockType.CHEST;
  }

  @Override
  public ILootrType getInfoNewType() {
    return BuiltInLootrTypes.POT;
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return lootrInstance.getInfoUUID();
  }

  @Override
  public String getInfoKey() {
    return lootrInstance.getInfoKey();
  }

  @Override
  public boolean hasBeenOpened() {
    return lootrInstance.hasBeenOpened();
  }

  @Override
  public boolean isPhysicallyOpen() {
    return false;
  }

  @Override
  public @NotNull BlockPos getInfoPos() {
    return getBlockPos();
  }

  @Override
  public @Nullable Component getInfoDisplayName() {
    return null;
  }

  @Override
  public @NotNull ResourceKey<Level> getInfoDimension() {
    return level.dimension();
  }

  @Override
  public int getInfoContainerSize() {
    return 1;
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
  public @Nullable ResourceKey<LootTable> getInfoLootTable() {
    return lootTable;
  }

  @Override
  public long getInfoLootSeed() {
    return lootTableSeed;
  }

  @Override
  public Level getInfoLevel() {
    return level;
  }

  @Override
  public @Nullable IContainerTrigger getTrigger() {
    return LootrRegistry.getPotTrigger();
  }

  @Override
  public double getParticleYOffset() {
    return 1.3;
  }

  @Override
  public double[] getParticleXBounds() {
    return new double[]{0.4, 0.6};
  }

  @Override
  public double[] getParticleZBounds() {
    return new double[]{0.4, 0.6};
  }

  @AutoService(ILootrBlockEntityConverter.class)
  public static class DefaultBlockEntityConverter implements ILootrBlockEntityConverter<LootrDecoratedPotBlockEntity> {
    @Override
    public ILootrBlockEntity apply(LootrDecoratedPotBlockEntity blockEntity) {
      return blockEntity;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
      return LootrRegistry.getDecoratedPotBlockEntity();
    }
  }
}
