package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

public class LootrBrushableBlockEntity extends BlockEntity implements ILootrBlockEntity, IBrushable {
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
    return this.saveCustomOnly(provider);
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

  private void tryLoadLootTable(ValueInput input) {
    this.lootTable = input.read("LootTable", LootTable.KEY_CODEC).orElse(null);
    this.lootTableSeed = input.getLongOr("LootTableSeed", 0L);
  }

  private void trySaveLootTable(ValueOutput output) {
    if (this.lootTable != null) {
      output.store("LootTable", LootTable.KEY_CODEC, this.lootTable);
      if (this.lootTableSeed != 0L) {
        output.putLong("LootTableSeed", this.lootTableSeed);
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

  @SuppressWarnings("deprecation")
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
    CompoundTag compoundTag = super.getUpdateTag(provider);
    compoundTag.storeNullable("hit_direction", Direction.LEGACY_ID_CODEC, this.hitDirection);

    Player player = this.getBrushingPlayer();
    if (player != null) {
      compoundTag.store("brushing_player", UUIDUtil.CODEC, player.getUUID());
      if (this.item.isEmpty()) {
        this.item = getItem(player);
      }
      if (!this.item.isEmpty()) {
        RegistryOps<Tag> registryops = provider.createSerializationContext(NbtOps.INSTANCE);
        compoundTag.store("item", ItemStack.CODEC, registryops, this.item);
      }
    }


    compoundTag.merge(this.simpleLootrInstance.fillUpdateTag(provider, level != null && level.isClientSide(), this));

    return compoundTag;
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
  }

  @Override
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    this.tryLoadLootTable(input);

    this.hitDirection = input.read("hit_direction", Direction.LEGACY_ID_CODEC).orElse(null);

    this.brushingPlayer = input.read("brushing_player", UUIDUtil.CODEC).orElse(null);
    this.brushingPlayerEntity = null;

    // This should only be on the client.
    if (this.brushingPlayer != null) {
      this.item = input.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    } else {
      this.item = ItemStack.EMPTY;
    }
    this.simpleLootrInstance.loadAdditional(input);
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    this.trySaveLootTable(output);
    this.simpleLootrInstance.saveAdditional(output, this.level != null && this.level.isClientSide());
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
    if (this.getBlockState().is(LootrTags.Blocks.SANDS)) {
      return BuiltInLootrTypes.SAND;
    } else if (this.getBlockState().is(LootrTags.Blocks.GRAVELS)) {
      return BuiltInLootrTypes.GRAVEL;
    } else {
      return BuiltInLootrTypes.CHEST;
    }
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
  public Level getInfoLevel() {
    return level;
  }

  @Override
  public void removeComponentsFromTag(ValueOutput compoundTag) {
    super.removeComponentsFromTag(compoundTag);
    compoundTag.discard("LootTable");
    compoundTag.discard("LootTableSeed");
    compoundTag.discard("LootrId");
  }

  @Override
  protected void applyImplicitComponents(DataComponentGetter dataComponentInput) {
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

  public static void fall(ServerLevel level, BlockPos blockPos, BlockState blockState, LootrBrushableBlockEntity brushableBlockEntity) {
    if (LootrAPI.canBrushablesSelfSupport()) {
      return;
    }
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
