package net.zestyblaze.lootr.block.entities;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.client.ClientHooks;
import net.zestyblaze.lootr.config.ConfigManager;
import net.zestyblaze.lootr.data.SpecialChestInventory;
import net.zestyblaze.lootr.init.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LootrBarrelBlockEntity extends RandomizableContainerBlockEntity implements ILootBlockEntity, RenderAttachmentBlockEntity {
  public Set<UUID> openers = new HashSet<>();
  protected ResourceLocation savedLootTable = null;
  protected long seed = -1;
  protected UUID tileId = null;
  protected boolean opened = false;
  private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
    @Override
    protected void onOpen(Level leve, BlockPos pos, BlockState state) {
      LootrBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
      LootrBarrelBlockEntity.this.updateBlockState(state, true);
    }

    @Override
    protected void onClose(Level level, BlockPos pos, BlockState state) {
      LootrBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
      LootrBarrelBlockEntity.this.updateBlockState(state, false);
    }

    @Override
    protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int p_155069_, int p_155070_) {
    }

    @Override
    protected boolean isOwnContainer(Player player) {
      if (player.containerMenu instanceof ChestMenu) {
        if (((ChestMenu) player.containerMenu).getContainer() instanceof SpecialChestInventory data) {
          if (data.getTileId() == null) {
            return data.getBlockEntity(LootrBarrelBlockEntity.this.getLevel()) == LootrBarrelBlockEntity.this;
          } else {
            return data.getTileId().equals(LootrBarrelBlockEntity.this.getTileId());
          }
        }
      }
      return false;
    }
  };
  private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
  private boolean savingToItem = false;

  public LootrBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(ModBlockEntities.SPECIAL_LOOT_BARREL, pWorldPosition, pBlockState);
  }

  @Override
  public UUID getTileId() {
    if (this.tileId == null) {
      this.tileId = UUID.randomUUID();
    }
    return this.tileId;
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
    super.setLootTable(lootTableIn, seedIn);
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return items;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> pItems) {
  }

  @Override
  public void unpackLootTable(@Nullable Player player) {
  }

  @Override
  @SuppressWarnings({"unused", "Duplicates"})
  public void unpackLootTable(Player player, Container inventory, @Nullable ResourceLocation overrideTable, long seed) {
    if (this.level != null && this.savedLootTable != null && this.level.getServer() != null) {
      LootTable loottable = this.level.getServer().getLootData().getLootTable(overrideTable != null ? overrideTable : this.savedLootTable);
      if (loottable == LootTable.EMPTY) {
        LootrAPI.LOG.error("Unable to fill loot barrel in " + level.dimension() + " at " + worldPosition + " as the loot table '" + (overrideTable != null ? overrideTable : this.savedLootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
        if (ConfigManager.get().debug.report_invalid_tables) {
          player.displayClientMessage(Component.translatable("lootr.message.invalid_table", (overrideTable != null ? overrideTable : this.savedLootTable).toString()).setStyle(ConfigManager.get().notifications.disable_message_styles ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)).withBold(true)), false);
        }
      }
      if (player instanceof ServerPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      LootParams.Builder builder = (new LootParams.Builder((ServerLevel) this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition));
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
      }
      loottable.fill(inventory, builder.create(LootContextParamSets.CHEST), LootrAPI.getLootSeed(seed == Long.MIN_VALUE ? this.seed : seed));
    }
  }

  @Override
  public ResourceLocation getTable() {
    return savedLootTable;
  }

  @Override
  public BlockPos getPosition() {
    return getBlockPos();
  }

  @Override
  public long getSeed() {
    return seed;
  }

  @Override
  public Set<UUID> getOpeners() {
    return openers;
  }

  @SuppressWarnings("Duplicates")
  @Override
  public void load(CompoundTag compound) {
    if (compound.contains("specialLootChest_table", Tag.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("specialLootChest_table"));
    }
    if (compound.contains("specialLootChest_seed", Tag.TAG_LONG)) {
      seed = compound.getLong("specialLootChest_seed");
    }
    if (savedLootTable == null && compound.contains("LootTable", Tag.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("LootTable"));
      if (compound.contains("LootTableSeed", Tag.TAG_LONG)) {
        seed = compound.getLong("LootTableSeed");
      }
      setLootTable(savedLootTable, seed);
    }
    if (compound.hasUUID("tileId")) {
      this.tileId = compound.getUUID("tileId");
    }
    if (this.tileId == null) {
      getTileId();
    }
    if (compound.contains("LootrOpeners")) {
      Set<UUID> newOpeners = new HashSet<>();
      ListTag openers = compound.getList("LootrOpeners", Tag.TAG_INT_ARRAY);
      for (Tag item : openers) {
        newOpeners.add(NbtUtils.loadUUID(item));
      }
      if (!Sets.symmetricDifference(this.openers, newOpeners).isEmpty()) {
        this.openers = newOpeners;
        if (this.getLevel() != null && this.getLevel().isClientSide()) {
          ClientHooks.clearCache(this.getBlockPos());
        }
      }
    }
    super.load(compound);
  }

  @Override
  public void saveToItem(ItemStack itemstack) {
    savingToItem = true;
    super.saveToItem(itemstack);
    savingToItem = false;
  }

  @Override
  protected void saveAdditional(CompoundTag compound) {
    super.saveAdditional(compound);
    if (savedLootTable != null) {
      compound.putString("LootTable", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.putLong("LootTableSeed", seed);
    }
    if (!LootrAPI.shouldDiscard() && !savingToItem) {
      compound.putUUID("tileId", getTileId());
      ListTag list = new ListTag();
      for (UUID opener : this.openers) {
        list.add(NbtUtils.createUUID(opener));
      }
      compound.put("LootrOpeners", list);
    }
  }

  @Override
  protected Component getDefaultName() {
    return Component.translatable("container.barrel");
  }

  @Override
  protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
    return null;
  }

  @Override
  public int getContainerSize() {
    return 27;
  }

  @Override
  public void startOpen(Player pPlayer) {
    if (!this.remove && !pPlayer.isSpectator()) {
      this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
    }

  }

  @Override
  public void stopOpen(Player pPlayer) {
    if (!this.remove && !pPlayer.isSpectator()) {
      this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
    }

  }

  public void recheckOpen() {
    if (!this.remove) {
      this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
    }

  }

  protected void updateBlockState(BlockState pState, boolean pOpen) {
    this.level.setBlock(this.getBlockPos(), pState.setValue(BarrelBlock.OPEN, pOpen), 3);
  }

  protected void playSound(BlockState pState, SoundEvent pSound) {
    Vec3i vec3i = pState.getValue(BarrelBlock.FACING).getNormal();
    double d0 = (double) this.worldPosition.getX() + 0.5D + (double) vec3i.getX() / 2.0D;
    double d1 = (double) this.worldPosition.getY() + 0.5D + (double) vec3i.getY() / 2.0D;
    double d2 = (double) this.worldPosition.getZ() + 0.5D + (double) vec3i.getZ() / 2.0D;
    this.level.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
  }

  @Override
  public void updatePacketViaState() {
    if (level != null && !level.isClientSide) {
      BlockState state = level.getBlockState(getBlockPos());
      level.sendBlockUpdated(getBlockPos(), state, state, 8);
    }
  }

  @Override
  public void setOpened(boolean opened) {
    this.opened = opened;
  }


  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag result = super.getUpdateTag();
    saveAdditional(result);
    return result;
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
  }

  @Override
  public @Nullable Object getRenderAttachmentData() {
    Player player = ClientHooks.getPlayer();
    if (player == null) {
      return null;
    }
    return getOpeners().contains(player.getUUID());
  }
}
