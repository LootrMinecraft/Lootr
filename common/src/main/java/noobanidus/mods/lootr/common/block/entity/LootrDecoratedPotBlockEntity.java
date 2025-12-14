package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.ILootrBlockEntityConverter;
import noobanidus.mods.lootr.common.api.ILootrType;
import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import noobanidus.mods.lootr.common.api.data.SimpleLootrInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class LootrDecoratedPotBlockEntity extends BlockEntity implements RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem, ILootrBlockEntity {
  public long wobbleStartedAtTick;
  @Nullable
  public DecoratedPotBlockEntity.WobbleStyle lastWobbleStyle;
  private PotDecorations decorations;
  @Nullable
  protected ResourceKey<LootTable> lootTable;
  protected long lootTableSeed;

  private final SimpleLootrInstance lootrInstance = new SimpleLootrInstance(this::getVisualOpeners, 1);

  public LootrDecoratedPotBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(LootrRegistry.getDecoratedPotBlockEntity(), blockPos, blockState);
    this.decorations = PotDecorations.EMPTY;
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
    super.saveAdditional(compoundTag, provider);
    this.decorations.save(compoundTag);
    this.lootrInstance.saveAdditional(compoundTag, provider, level == null || level.isClientSide());
  }

  @Override
  protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
    super.loadAdditional(compoundTag, provider);
    this.decorations = PotDecorations.load(compoundTag);
    this.tryLoadLootTable(compoundTag);
    this.lootrInstance.loadAdditional(compoundTag, provider);
  }

  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
    CompoundTag compoundTag = super.getUpdateTag(provider);
    this.lootrInstance.fillUpdateTag(compoundTag, provider, level != null && level.isClientSide());
    return compoundTag;
  }

  public Direction getDirection() {
    return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
  }

  public PotDecorations getDecorations() {
    return this.decorations;
  }

  public void setFromItem(ItemStack itemStack) {
    this.applyComponentsFromItemStack(itemStack);
  }

  public ItemStack getPotAsItem() {
    ItemStack itemStack = Items.DECORATED_POT.getDefaultInstance();
    itemStack.applyComponents(this.collectComponents());
    return itemStack;
  }

  public static ItemStack createDecoratedPotItem(PotDecorations potDecorations) {
    ItemStack itemStack = Items.DECORATED_POT.getDefaultInstance();
    itemStack.set(DataComponents.POT_DECORATIONS, potDecorations);
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
    builder.set(DataComponents.POT_DECORATIONS, this.decorations);
  }

  @Override
  protected void applyImplicitComponents(BlockEntity.DataComponentInput dataComponentInput) {
    super.applyImplicitComponents(dataComponentInput);
    this.decorations = dataComponentInput.getOrDefault(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY);
  }

  @Override
  public void removeComponentsFromTag(CompoundTag compoundTag) {
    super.removeComponentsFromTag(compoundTag);
    compoundTag.remove("sherds");
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
