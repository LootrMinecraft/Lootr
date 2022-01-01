package noobanidus.mods.lootr.block.tile;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
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

public class LootrShulkerTileEntity extends LockableLootTileEntity implements ILootTile, ITickableTileEntity {
  public Set<UUID> openers = new HashSet<>();
  private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
  private int openCount;
  private ShulkerBoxTileEntity.AnimationStatus animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSED;
  private float progress;
  private float progressOld;
  private ResourceLocation savedLootTable = null;
  private long seed = -1;
  private UUID tileId = null;
  private boolean opened;

  public LootrShulkerTileEntity() {
    super(ModTiles.LOOK_SHULKER);
  }

  @Override
  public void tick() {
    this.updateAnimation();
    if (this.animationStatus == ShulkerBoxTileEntity.AnimationStatus.OPENING || this.animationStatus == ShulkerBoxTileEntity.AnimationStatus.CLOSING) {
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
          this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.OPENED;
          this.progress = 1.0F;
          this.doNeighborUpdates();
        }
        break;
      case CLOSING:
        this.progress -= 0.1F;
        if (this.progress <= 0.0F) {
          this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSED;
          this.progress = 0.0F;
          this.doNeighborUpdates();
        }
        break;
      case OPENED:
        this.progress = 1.0F;
    }

  }

  public ShulkerBoxTileEntity.AnimationStatus getAnimationStatus() {
    return this.animationStatus;
  }

  public AxisAlignedBB getBoundingBox(BlockState pState) {
    return this.getBoundingBox(pState.getValue(ShulkerBoxBlock.FACING));
  }

  public AxisAlignedBB getBoundingBox(Direction p_190587_1_) {
    float f = this.getProgress(1.0F);
    return VoxelShapes.block().bounds().expandTowards(0.5F * f * (float) p_190587_1_.getStepX(), 0.5F * f * (float) p_190587_1_.getStepY(), 0.5F * f * (float) p_190587_1_.getStepZ());
  }

  private AxisAlignedBB getTopBoundingBox(Direction p_190588_1_) {
    Direction direction = p_190588_1_.getOpposite();
    return this.getBoundingBox(p_190588_1_).contract(direction.getStepX(), direction.getStepY(), direction.getStepZ());
  }

  private void moveCollidedEntities() {
    BlockState blockstate = this.level.getBlockState(this.getBlockPos());
    if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
      Direction direction = blockstate.getValue(ShulkerBoxBlock.FACING);
      AxisAlignedBB axisalignedbb = this.getTopBoundingBox(direction).move(this.worldPosition);
      List<Entity> list = this.level.getEntities(null, axisalignedbb);
      if (!list.isEmpty()) {
        for (Entity entity : list) {
          if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 0.0D;
            AxisAlignedBB axisalignedbb1 = entity.getBoundingBox();
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

            entity.move(MoverType.SHULKER_BOX, new Vector3d(d0 * (double) direction.getStepX(), d1 * (double) direction.getStepY(), d2 * (double) direction.getStepZ()));
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
        this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSING;
        this.doNeighborUpdates();
      }

      if (pType == 1) {
        this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.OPENING;
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
  public void startOpen(PlayerEntity pPlayer) {
    if (!pPlayer.isSpectator()) {
      if (this.openCount < 0) {
        this.openCount = 0;
      }

      ++this.openCount;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
      if (this.openCount == 1) {
        this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
    }

  }

  @Override
  public void stopOpen(PlayerEntity pPlayer) {
    if (!pPlayer.isSpectator()) {
      --this.openCount;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
      if (this.openCount <= 0) {
        this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
      openers.add(pPlayer.getUUID());
      updatePacketViaState();
    }

  }

  @Override
  protected ITextComponent getDefaultName() {
    return new TranslationTextComponent("container.shulkerBox");
  }

  @Override
  public void load(BlockState state, CompoundNBT compound) {
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
      ListNBT openers = compound.getList("LootrOpeners", Constants.NBT.TAG_INT_ARRAY);
      this.openers.clear();
      for (INBT item : openers) {
        this.openers.add(NBTUtil.loadUUID(item));
      }
    }
    requestModelDataUpdate();
    super.load(state, compound);
  }

  @Override
  public CompoundNBT save(CompoundNBT compound) {
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
    ListNBT list = new ListNBT();
    for (UUID opener : this.openers) {
      list.add(NBTUtil.createUUID(opener));
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
    return MathHelper.lerp(pPartialTicks, this.progressOld, this.progress);
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
    super.setLootTable(lootTableIn, seedIn);
  }

  @Override
  public void unpackLootTable(@Nullable PlayerEntity pPlayer) {
  }

  @Nullable
  @OnlyIn(Dist.CLIENT)
  public DyeColor getColor() {
    return DyeColor.YELLOW;
  }

  @Override
  protected Container createMenu(int pId, PlayerInventory pPlayer) {
    return new ShulkerBoxContainer(pId, pPlayer, this);
  }

  public boolean isClosed() {
    return this.animationStatus == ShulkerBoxTileEntity.AnimationStatus.CLOSED;
  }

  @Override
  public void fillWithLoot(PlayerEntity player, IInventory inventory, ResourceLocation overrideTable, long seed) {
    if (this.level != null && this.savedLootTable != null && this.level.getServer() != null) {
      LootTable loottable = this.level.getServer().getLootTables().get(overrideTable != null ? overrideTable : this.savedLootTable);
      if (loottable == LootTable.EMPTY) {
        Lootr.LOG.error("Unable to fill loot shulker in " + level.dimension() + " at " + worldPosition + " as the loot table '" + (overrideTable != null ? overrideTable : this.savedLootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
        if (ConfigManager.REPORT_UNRESOLVED_TABLES.get()) {
          player.sendMessage(new TranslationTextComponent("lootr.message.invalid_table", (overrideTable != null ? overrideTable : this.savedLootTable).toString()).setStyle(Style.EMPTY.withColor(TextFormatting.DARK_RED).withBold(true)), Util.NIL_UUID);
        }
      }
      if (player instanceof ServerPlayerEntity) {
        CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayerEntity) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.level)).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(this.worldPosition)).withOptionalRandomSeed(ConfigManager.RANDOMISE_SEED.get() ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.seed : seed);
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
      }

      loottable.fill(inventory, builder.create(LootParameterSets.CHEST));
    }
  }

  @Override
  public ResourceLocation getTable() {
    return savedLootTable;
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
  public CompoundNBT getUpdateTag() {
    return save(new CompoundNBT());
  }

  @Override
  @Nullable
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
  }

  @Override
  public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SUpdateTileEntityPacket pkt) {
    load(ModBlocks.SHULKER.defaultBlockState(), pkt.getTag());
  }

  public boolean isOpened() {
    return opened;
  }

  public void setOpened(boolean opened) {
    this.opened = opened;
  }
}
