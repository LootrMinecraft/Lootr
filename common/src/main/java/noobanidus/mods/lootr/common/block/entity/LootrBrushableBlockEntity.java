package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.*;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import noobanidus.mods.lootr.common.api.data.SimpleLootrInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.integration.digsite_workshop.IModdedBrushItem;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinFallingBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

// Abstract so that platform-specific extensions can handle client-side
// rendering.
public abstract class  LootrBrushableBlockEntity extends BlockEntity implements ILootrBlockEntity, IBrushable {
  private final SimpleLootrInstance simpleLootrInstance = new SimpleLootrInstance(this::getVisualOpeners, 1);

  @Nullable
  private UUID brushingPlayer;
  @Nullable
  private Player brushingPlayerEntity;

  private int brushCount;
  private long brushCountResetsAtTick;
  private long coolDownEndsAtTick;

  private ItemStack item = ItemStack.EMPTY;

  @Nullable
  private Direction hitDirection;

  @Nullable
  private ResourceKey<LootTable> lootTable;
  private long lootTableSeed;

  public LootrBrushableBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(LootrRegistry.getBrushableBlockEntity(), blockPos, blockState);
  }

  private CompoundTag getFallData(HolderLookup.Provider provider) {
    CompoundTag tag = new CompoundTag();
    saveAdditional(tag, provider);
    return tag;
  }

  @Override
  public @Nullable IContainerTrigger getTrigger() {
    if (getBlockState().is(LootrTags.Blocks.SANDS)) {
      return LootrRegistry.getSandTrigger();
    } else if (getBlockState().is(LootrTags.Blocks.GRAVELS)) {
      return LootrRegistry.getGravelTrigger();
    } else {
      return null;
    }
  }

  @Override
  public boolean IBrushable$brush(long l, Player player, Direction direction) {
    // This code mimics `FasterBrushingMixin` from Digsite Workshop.
    ItemStack brushItem = getBrushItem(player);
    if (brushItem.getItem() instanceof IModdedBrushItem moddedBrushItem) {
      this.coolDownEndsAtTick -= 10L - moddedBrushItem.lootr$getBrushingSpeed();
    }

    Player brushingPlayer = this.getBrushingPlayer();
    if (brushingPlayer != null) {
      if (player != brushingPlayer) {
        return false;
      }
      if (!hasLootAvailable((ServerPlayer) player)) {
        return false;
      }
    } else {
      if (!hasLootAvailable((ServerPlayer) player)) {
        this.brushingPlayer = null;
        this.brushingPlayerEntity = null;
        return false;
      }
      this.brushingPlayerEntity = player;
      this.brushingPlayer = null;
    }

    if (!this.simpleLootrInstance.hasBeenOpened()) {
      this.simpleLootrInstance.setHasBeenOpened();
      markChanged();
    }

    if (this.hitDirection == null) {
      this.hitDirection = direction;
    }

    this.brushCountResetsAtTick = l + 40L;
    if (l >= this.coolDownEndsAtTick && this.level instanceof ServerLevel) {
      this.coolDownEndsAtTick = l + 10L;
      int i = this.getCompletionState();
      if (++this.brushCount >= 10) {
        this.brushingCompleted(player);
        return true;
      } else {
        this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 2);
        int j = this.getCompletionState();
        if (i != j) {
          BlockState blockState = this.getBlockState();
          BlockState blockState2 = blockState.setValue(BlockStateProperties.DUSTED, j);
          this.level.setBlock(this.getBlockPos(), blockState2, 3);
        }

        return false;
      }
    } else {
      return false;
    }
  }

  private ItemStack getBrushItem(Player player) {
    if (player.getMainHandItem().getItem() instanceof IModdedBrushItem) {
      return player.getMainHandItem();
    } else if (player.getOffhandItem().getItem() instanceof IModdedBrushItem) {
      return player.getOffhandItem();
    }
    return ItemStack.EMPTY;
  }

  private void brushingCompleted(Player player) {
    if (this.level != null && this.level.getServer() != null) {
      this.dropContent(player);
      this.performTrigger((ServerPlayer) player);
      boolean shouldUpdate = false;
      if (!this.hasServerOpened(player)) {
        player.awardStat(LootrRegistry.getLootedStat());
        LootrRegistry.getStatTrigger().trigger((ServerPlayer) player);
      }
      if (this.addOpener(player)) {
        this.performOpen((ServerPlayer) player);
        shouldUpdate = true;
      }

      if (shouldUpdate) {
        this.performUpdate((ServerPlayer) player);
      }
    }
  }

  private void dropContent(Player player) {
    if (this.level != null && this.level.getServer() != null) {
      ItemStack theItem = this.popItem(player);
      if (!theItem.isEmpty()) {
        double d = EntityType.ITEM.getWidth();
        double e = 1.0 - d;
        double f = d / 2.0;
        Direction direction = Objects.requireNonNullElse(this.hitDirection, Direction.UP);
        BlockPos blockPos = this.worldPosition.relative(direction, 1);
        double g = (double) blockPos.getX() + 0.5 * e + f;
        double h = (double) blockPos.getY() + 0.5 + (double) (EntityType.ITEM.getHeight() / 2.0F);
        double i = (double) blockPos.getZ() + 0.5 * e + f;
        ItemEntity itemEntity = new ItemEntity(this.level, g, h, i, theItem.split(this.level.random.nextInt(21) + 10));
        itemEntity.setDeltaMovement(Vec3.ZERO);
        this.level.addFreshEntity(itemEntity);
        this.item = ItemStack.EMPTY;
      }
    }
  }

  @Override
  public void IBrushable$checkReset() {
    if (this.level != null) {
      if (this.brushCount != 0 && this.level.getGameTime() >= this.brushCountResetsAtTick) {
        int i = this.getCompletionState();
        this.brushCount = Math.max(0, this.brushCount - 2);
        int j = this.getCompletionState();
        if (i != j) {
          this.level.setBlock(this.getBlockPos(), this.getBlockState()
              .setValue(BlockStateProperties.DUSTED, j), 3);
        }

        this.brushCountResetsAtTick = this.level.getGameTime() + 4L;
      }

      if (this.brushCount == 0) {
        this.brushingPlayer = null;
        this.brushingPlayerEntity = null;
        this.hitDirection = null;
        this.brushCountResetsAtTick = 0L;
        this.coolDownEndsAtTick = 0L;
      } else {
        this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 2);
      }
    }
  }

  private void tryLoadLootTable(CompoundTag compoundTag) {
    if (compoundTag.contains("LootTable")) {
      this.lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(compoundTag.getString("LootTable")));
    }
    if (compoundTag.contains("LootTableSeed")) {
      this.lootTableSeed = compoundTag.getLong("LootTableSeed");
    }
  }

  private void trySaveLootTable(CompoundTag compoundTag) {
    if (this.lootTable != null) {
      compoundTag.putString("LootTable", this.lootTable.location().toString());
      if (this.lootTableSeed != 0L) {
        compoundTag.putLong("LootTableSeed", this.lootTableSeed);
      }
    }
  }

  @Override
  public void setLootTableInternal(ResourceKey<LootTable> lootTable, long seed) {
    this.lootTable = lootTable;
    this.lootTableSeed = seed;
  }

  @Nullable
  public Player getBrushingPlayer() {
    if (this.brushingPlayerEntity != null) {
      if (this.brushingPlayer != null) {
        this.brushingPlayer = null;
      }

      return this.brushingPlayerEntity;
    } else if (this.brushingPlayer != null) {
      if (this.level != null) {
        this.brushingPlayerEntity = this.level.getPlayerByUUID(this.brushingPlayer);
        this.brushingPlayer = null;
      }
    }

    return null;
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
    CompoundTag compoundTag = super.getUpdateTag(provider);
    if (this.hitDirection != null) {
      compoundTag.putInt("hit_direction", this.hitDirection.ordinal());
    }

    Player player = this.getBrushingPlayer();
    if (player != null) {
      compoundTag.putUUID("brushing_player", player.getUUID());
      if (this.item.isEmpty()) {
        this.item = getItem(player);
      }
      if (!this.item.isEmpty()) {
        compoundTag.put("item", this.item.save(provider));
      }
    }

    this.simpleLootrInstance.fillUpdateTag(compoundTag, provider, level != null && level.isClientSide());

    return compoundTag;
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
  }

  @Override
  protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
    super.loadAdditional(compoundTag, provider);
    this.tryLoadLootTable(compoundTag);

    if (compoundTag.contains("hit_direction")) {
      this.hitDirection = Direction.values()[compoundTag.getInt("hit_direction")];
    }

    if (compoundTag.hasUUID("brushing_player")) {
      this.brushingPlayer = compoundTag.getUUID("brushing_player");
    } else {
      this.brushingPlayer = null;
    }
    this.brushingPlayerEntity = null;

    // This should only be on the client.
    if (this.brushingPlayer != null && compoundTag.contains("item")) {
      this.item = ItemStack.parseOptional(provider, compoundTag.getCompound("item"));
    } else {
      this.item = ItemStack.EMPTY;
    }
    this.simpleLootrInstance.loadAdditional(compoundTag, provider);
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
    super.saveAdditional(compoundTag, provider);
    this.trySaveLootTable(compoundTag);
    this.simpleLootrInstance.saveAdditional(compoundTag, provider, this.level != null && this.level.isClientSide());
  }

  private int getCompletionState() {
    if (this.brushCount == 0) {
      return 0;
    } else if (this.brushCount < 3) {
      return 1;
    } else {
      return this.brushCount < 6 ? 2 : 3;
    }
  }

  @Nullable
  public Direction getHitDirection() {
    return this.hitDirection;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public boolean isBrushingPlayer(Player player) {
    Player brushingPlayer = getBrushingPlayer();
    return brushingPlayer != null && brushingPlayer == player;
  }

  public ItemStack getItem(Player player) {
    boolean clientSide = player.level().isClientSide();

    if (isBrushingPlayer(player)) {
      if (clientSide) {
        return this.item; // Rely on it being sync'd from the server
      } else {
        if (this.item.isEmpty()) {
          // Set the item from the player's container
          ILootrInventory inventory = LootrAPI.getInventory(this, (ServerPlayer) player);
          if (inventory == null) {
            this.item = ItemStack.EMPTY;
          } else {
            this.item = inventory.getItem(0);
          }
        }
      }
    } else {
      // Always set it to empty if the player is not the brushing player
      this.item = ItemStack.EMPTY;
    }

    return this.item;
  }

  private ItemStack popItem(Player player) {
    if (player.level().isClientSide()) {
      return getItem(player);
    } else {
      ItemStack theItem = getItem(player);
      if (!theItem.isEmpty()) {
        // Clear the item from the player's container
        ILootrInventory inventory = LootrAPI.getInventory(this, (ServerPlayer) player);
        if (inventory != null) {
          inventory.setItem(0, ItemStack.EMPTY);
          inventory.setChanged();
        }
        this.item = ItemStack.EMPTY;
      }
      return theItem;
    }
  }

  @Override
  public @Nullable Set<UUID> getClientOpeners() {
    return this.simpleLootrInstance.getClientOpeners();
  }

  @Override
  public boolean isClientOpened() {
    return this.simpleLootrInstance.isClientOpened();
  }

  @Override
  public void setClientOpened(boolean opened) {
    this.simpleLootrInstance.setClientOpened(opened);
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
    return simpleLootrInstance.getInfoUUID();
  }

  @Override
  public String getInfoKey() {
    return simpleLootrInstance.getInfoKey();
  }

  @Override
  public boolean hasBeenOpened() {
    return simpleLootrInstance.hasBeenOpened();
  }

  @Override
  public boolean isPhysicallyOpen() {
    // TODO: This should be more complex
    return this.getBlockState().getValue(BlockStateProperties.DUSTED) > 0;
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
    return this.level.dimension();
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
  public void removeComponentsFromTag(CompoundTag compoundTag) {
    super.removeComponentsFromTag(compoundTag);
    compoundTag.remove("LootTable");
    compoundTag.remove("LootTableSeed");
  }

  @Override
  protected void applyImplicitComponents(BlockEntity.DataComponentInput dataComponentInput) {
    super.applyImplicitComponents(dataComponentInput);
    SeededContainerLoot loot = dataComponentInput.get(DataComponents.CONTAINER_LOOT);
    if (loot != null && loot.lootTable() != null) {
      this.lootTable = loot.lootTable();
      this.lootTableSeed = loot.seed();
    }
  }

  @Override
  protected void collectImplicitComponents(DataComponentMap.Builder builder) {
    super.collectImplicitComponents(builder);
    if (lootTable != null) {
      builder.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(lootTable, lootTableSeed));
    }
  }

  public static FallingBlockEntity fall(ServerLevel level, BlockPos blockPos, BlockState blockState, LootrBrushableBlockEntity brushableBlockEntity) {
    FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(EntityType.FALLING_BLOCK, level);
    double d = (double) blockPos.getX() + 0.5;
    double e = blockPos.getY();
    double f = (double) blockPos.getZ() + 0.5;
    ((AccessorMixinFallingBlockEntity) fallingBlockEntity).lootr$setBlockState(blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE) : blockState);
    fallingBlockEntity.blocksBuilding = true;
    fallingBlockEntity.setPos(d, e, f);
    fallingBlockEntity.setDeltaMovement(Vec3.ZERO);
    fallingBlockEntity.xo = d;
    fallingBlockEntity.yo = e;
    fallingBlockEntity.zo = f;
    fallingBlockEntity.blockData = brushableBlockEntity.getFallData(level.registryAccess());
    fallingBlockEntity.setStartPos(fallingBlockEntity.blockPosition());
    fallingBlockEntity.dropItem = false;
    level.setBlock(blockPos, blockState.getFluidState().createLegacyBlock(), 3);
    level.addFreshEntity(fallingBlockEntity);
    return fallingBlockEntity;
  }


  @AutoService(ILootrBlockEntityConverter.class)
  public static class DefaultBlockEntityConverter implements ILootrBlockEntityConverter<LootrBrushableBlockEntity> {
    @Override
    public ILootrBlockEntity apply(LootrBrushableBlockEntity blockEntity) {
      return blockEntity;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
      return LootrRegistry.getBrushableBlockEntity();
    }
  }
}
