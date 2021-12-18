package noobanidus.mods.lootr.tiles;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModTiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class SpecialLootShulkerTile extends RandomizableContainerBlockEntity implements ILootTile, TickableBlockEntity {
  public Set<UUID> openers = new HashSet<>();
  private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
  private int openCount;
  private ShulkerBoxBlockEntity.AnimationStatus animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  private float progress;
  private float progressOld;
  private ResourceLocation savedLootTable = null;
  private long seed = -1;
  private UUID tileId = null;
  private boolean opened;

  public SpecialLootShulkerTile() {
    super(ModTiles.SPECIAL_LOOT_SHULKER);
  }

  @Override
  public void tick() {
    this.updateAnimation();
    if (this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.OPENING || this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSING) {
      this.moveCollidedEntities();
    }
  }

  protected void updateAnimation() {
    this.progressOld = this.progress;
    switch (this.animationStatus) {
      case CLOSED:
        this.progress = 0.0F;
        break;
      case OPENING:
        this.progress += 0.1F;
        if (this.progress >= 1.0F) {
          this.moveCollidedEntities();
          this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
          this.progress = 1.0F;
          this.doNeighborUpdates();
        }
        break;
      case CLOSING:
        this.progress -= 0.1F;
        if (this.progress <= 0.0F) {
          this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
          this.progress = 0.0F;
          this.doNeighborUpdates();
        }
        break;
      case OPENED:
        this.progress = 1.0F;
    }

  }

  public ShulkerBoxBlockEntity.AnimationStatus getAnimationStatus() {
    return this.animationStatus;
  }

  public AABB getBoundingBox(BlockState pState) {
    return this.getBoundingBox(pState.getValue(ShulkerBoxBlock.FACING));
  }

  public AABB getBoundingBox(Direction p_190587_1_) {
    float f = this.getProgress(1.0F);
    return Shapes.block().bounds().expandTowards(0.5F * f * (float) p_190587_1_.getStepX(), 0.5F * f * (float) p_190587_1_.getStepY(), 0.5F * f * (float) p_190587_1_.getStepZ());
  }

  private AABB getTopBoundingBox(Direction p_190588_1_) {
    Direction direction = p_190588_1_.getOpposite();
    return this.getBoundingBox(p_190588_1_).contract(direction.getStepX(), direction.getStepY(), direction.getStepZ());
  }

  private void moveCollidedEntities() {
    BlockState blockstate = this.level.getBlockState(this.getBlockPos());
    if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
      Direction direction = blockstate.getValue(ShulkerBoxBlock.FACING);
      AABB axisalignedbb = this.getTopBoundingBox(direction).move(this.worldPosition);
      List<Entity> list = this.level.getEntities(null, axisalignedbb);
      if (!list.isEmpty()) {
        for (Entity entity : list) {
          if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 0.0D;
            AABB axisalignedbb1 = entity.getBoundingBox();
            switch (direction.getAxis()) {
              case X:
                if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                  d0 = axisalignedbb.maxX - axisalignedbb1.minX;
                } else {
                  d0 = axisalignedbb1.maxX - axisalignedbb.minX;
                }

                d0 = d0 + 0.01D;
                break;
              case Y:
                if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                  d1 = axisalignedbb.maxY - axisalignedbb1.minY;
                } else {
                  d1 = axisalignedbb1.maxY - axisalignedbb.minY;
                }

                d1 = d1 + 0.01D;
                break;
              case Z:
                if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                  d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
                } else {
                  d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
                }

                d2 = d2 + 0.01D;
            }

            entity.move(MoverType.SHULKER_BOX, new Vec3(d0 * (double) direction.getStepX(), d1 * (double) direction.getStepY(), d2 * (double) direction.getStepZ()));
          }
        }
      }
    }
  }

  @Override
  public int getContainerSize() {
    return 27;
  }

  @Override
  public boolean triggerEvent(int pId, int pType) {
    if (pId == 1) {
      this.openCount = pType;
      if (pType == 0) {
        this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSING;
        this.doNeighborUpdates();
      }

      if (pType == 1) {
        this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENING;
        this.doNeighborUpdates();
      }

      return true;
    } else {
      return super.triggerEvent(pId, pType);
    }
  }

  private void doNeighborUpdates() {
    this.getBlockState().updateNeighbourShapes(this.getLevel(), this.getBlockPos(), 3);
  }

  @Override
  public void startOpen(Player pPlayer) {
    if (!pPlayer.isSpectator()) {
      if (this.openCount < 0) {
        this.openCount = 0;
      }

      ++this.openCount;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
      if (this.openCount == 1) {
        this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
    }

  }

  @Override
  public void stopOpen(Player pPlayer) {
    if (!pPlayer.isSpectator()) {
      --this.openCount;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
      if (this.openCount <= 0) {
        this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
      openers.add(pPlayer.getUUID());
      updatePacketViaState();
    }

  }

  @Override
  protected Component getDefaultName() {
    return new TranslatableComponent("container.shulkerBox");
  }

  @Override
  public void load(BlockState state, CompoundTag compound) {
    if (compound.contains("specialLootChest_table", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("specialLootChest_table"));
    }
    if (compound.contains("specialLootChest_seed", Constants.NBT.TAG_LONG)) {
      seed = compound.getLong("specialLootChest_seed");
    }
    if (savedLootTable == null && compound.contains("LootTable", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("LootTable"));
      if (compound.contains("LootTableSeed", Constants.NBT.TAG_LONG)) {
        seed = compound.getLong("LootTableSeed");
      }
      setLootTable(savedLootTable, seed);
    }
    if (compound.hasUUID("tileId")) {
      this.tileId = compound.getUUID("tileId");
    } else if (this.tileId == null) {
      getTileId();
    }
    if (compound.contains("LootrOpeners")) {
      ListTag openers = compound.getList("LootrOpeners", Constants.NBT.TAG_INT_ARRAY);
      this.openers.clear();
      for (Tag item : openers) {
        this.openers.add(NbtUtils.loadUUID(item));
      }
    }
    requestModelDataUpdate();
    super.load(state, compound);
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
    compound = super.save(compound);
    if (savedLootTable != null) {
      compound.putString("specialLootBarrel_table", savedLootTable.toString());
      compound.putString("LootTable", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.putLong("specialLootBarrel_seed", seed);
      compound.putLong("LootTableSeed", seed);
    }
    compound.putUUID("tileId", getTileId());
    ListTag list = new ListTag();
    for (UUID opener : this.openers) {
      list.add(NbtUtils.createUUID(opener));
    }
    compound.put("LootrOpeners", list);
    return compound;
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    return LazyOptional.empty();
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return this.itemStacks;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> pItems) {
  }

  public float getProgress(float pPartialTicks) {
    return Mth.lerp(pPartialTicks, this.progressOld, this.progress);
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
    super.setLootTable(lootTableIn, seedIn);
  }

  @Override
  public void unpackLootTable(@Nullable Player pPlayer) {
  }

  @Nullable
  @OnlyIn(Dist.CLIENT)
  public DyeColor getColor() {
    return DyeColor.YELLOW;
  }

  @Override
  protected AbstractContainerMenu createMenu(int pId, Inventory pPlayer) {
    return new ShulkerBoxMenu(pId, pPlayer, this);
  }

  public boolean isClosed() {
    return this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  }

  @Override
  public void fillWithLoot(Player player, Container inventory, ResourceLocation overrideTable, long seed) {
    if (this.level != null && this.savedLootTable != null && this.level.getServer() != null) {
      LootTable loottable = this.level.getServer().getLootTables().get(overrideTable != null ? overrideTable : this.savedLootTable);
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
  public long getSeed() {
    return seed;
  }

  @Override
  public Set<UUID> getOpeners() {
    return openers;
  }

  @Override
  public UUID getTileId() {
    if (this.tileId == null) {
      this.tileId = UUID.randomUUID();
    }
    return this.tileId;
  }

  @Override
  public void updatePacketViaState() {
    if (level != null && !level.isClientSide) {
      BlockState state = level.getBlockState(getBlockPos());
      level.sendBlockUpdated(getBlockPos(), state, state, 8);
    }
  }

  @Override
  @Nonnull
  public CompoundTag getUpdateTag() {
    return save(new CompoundTag());
  }

  @Override
  @Nullable
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return new ClientboundBlockEntityDataPacket(getBlockPos(), 0, getUpdateTag());
  }

  @Override
  public void onDataPacket(@Nonnull Connection net, @Nonnull ClientboundBlockEntityDataPacket pkt) {
    load(ModBlocks.SHULKER.defaultBlockState(), pkt.getTag());
  }

  public boolean isOpened() {
    return opened;
  }

  public void setOpened(boolean opened) {
    this.opened = opened;
  }
}
