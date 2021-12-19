package noobanidus.mods.lootr.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.init.ModBlockEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LootrInventoryBlockEntity extends LootrChestBlockEntity {
  private NonNullList<ItemStack> customInventory;

  protected LootrInventoryBlockEntity(BlockEntityType<?> p_155327_, BlockPos p_155328_, BlockState p_155329_) {
    super(p_155327_, p_155328_, p_155329_);
  }

  public LootrInventoryBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(ModBlockEntities.SPECIAL_LOOT_INVENTORY, pWorldPosition, pBlockState);
  }

  @Override
  public void load(CompoundTag compound) {
    super.load(compound);
    if (compound.contains("customInventory") && compound.contains("customSize")) {
      int size = compound.getInt("customSize");
      this.customInventory = NonNullList.withSize(size, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compound.getCompound("customInventory"), this.customInventory);
    }
  }

  @Override
  protected void saveAdditional (CompoundTag compound) {
    super.saveAdditional(compound);
    if (this.customInventory != null) {
      compound.putInt("customSize", this.customInventory.size());
      compound.put("customInventory", ContainerHelper.saveAllItems(new CompoundTag(), this.customInventory));
    }
  }

  @Nullable
  public NonNullList<ItemStack> getCustomInventory() {
    return customInventory;
  }

  public void setCustomInventory(NonNullList<ItemStack> customInventory) {
    this.customInventory = customInventory;
  }

  @Override
  public void onDataPacket(@Nonnull Connection net, @Nonnull ClientboundBlockEntityDataPacket pkt) {
    load(pkt.getTag());
  }
}
