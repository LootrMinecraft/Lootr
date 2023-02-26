package noobanidus.mods.lootr.block.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.init.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class LootrInventoryBlockEntity extends LootrChestBlockEntity {
  private NonNullList<ItemStack> customInventory;

  protected LootrInventoryBlockEntity(BlockEntityType<?> entity, BlockPos pos, BlockState state) {
    super(entity, pos, state);
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
  protected void saveAdditional(CompoundTag compound) {
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
    /*
    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

     */
}
