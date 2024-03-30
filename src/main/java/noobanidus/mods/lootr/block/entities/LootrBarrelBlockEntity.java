package noobanidus.mods.lootr.block.entities;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.LootrBarrelBlock;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.SpecialChestInventory;
import noobanidus.mods.lootr.init.ModBlockEntities;
import noobanidus.mods.lootr.util.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LootrBarrelBlockEntity extends RandomizableContainerBlockEntity implements ILootBlockEntity {
  public Set<UUID> openers = new HashSet<>();
  protected ResourceLocation savedLootTable = null;
  protected long seed = -1;
  protected UUID tileId = null;
  protected boolean opened = false;
  private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
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

    // TODO: Potentially related to #228, this implementation is searching for `ChestMenu` instances whereas our MenuProvider is theoretically `SpecialChestInventory`.
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

  public LootrBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(ModBlockEntities.SPECIAL_LOOT_BARREL, pWorldPosition, pBlockState);
  }

  private IModelData modelData = null;

  @Nonnull
  @Override
  public IModelData getModelData() {
    if (modelData == null) {
      modelData = new ModelDataMap.Builder().withInitial(LootrBarrelBlock.OPENED, false).build();
    }
    Player player = Getter.getPlayer();
    if (player != null) {
      modelData.setData(LootrBarrelBlock.OPENED, openers.contains(player.getUUID()));
    }
    return modelData;
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

  private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);

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
      LootTable loottable = this.level.getServer().getLootTables().get(overrideTable != null ? overrideTable : this.savedLootTable);
      if (loottable == LootTable.EMPTY) {
        LootrAPI.LOG.error("Unable to fill loot barrel in " + level.dimension() + " at " + worldPosition + " as the loot table '" + (overrideTable != null ? overrideTable : this.savedLootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
        if (ConfigManager.REPORT_UNRESOLVED_TABLES.get()) {
          player.sendMessage(new TranslatableComponent("lootr.message.invalid_table", (overrideTable != null ? overrideTable : this.savedLootTable).toString()).setStyle(ConfigManager.DISABLE_MESSAGE_STYLES.get() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)).withBold(true)), Util.NIL_UUID);
        }
      }
      if (player instanceof ServerPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      LootContext.Builder builder = (new LootContext.Builder((ServerLevel) this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withOptionalRandomSeed(ConfigManager.RANDOMISE_SEED.get() ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.seed : seed);
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
      }

      loottable.fill(inventory, builder.create(LootContextParamSets.CHEST));
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
      ListTag openers = compound.getList("LootrOpeners", Tag.TAG_INT_ARRAY);
      this.openers.clear();
      for (Tag item : openers) {
        this.openers.add(NbtUtils.loadUUID(item));
      }
    }
    requestModelDataUpdate();
    super.load(compound);
  }

  private boolean savingToItem = false;

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
      compound.putString("specialLootBarrel_table", savedLootTable.toString());
      compound.putString("LootTable", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.putLong("specialLootBarrel_seed", seed);
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
    return new TranslatableComponent("container.barrel");
  }

  @Override
  protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
    return null;
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    return LazyOptional.empty();
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
  @Nonnull
  public CompoundTag getUpdateTag() {
    CompoundTag result = super.getUpdateTag();
    saveAdditional(result);
    return result;
  }

  @Override
  @Nullable
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
  }

  @Override
  public void onDataPacket(@Nonnull Connection net, @Nonnull ClientboundBlockEntityDataPacket pkt) {
    if (pkt.getTag() != null) {
      load(pkt.getTag());
    }
  }
}
