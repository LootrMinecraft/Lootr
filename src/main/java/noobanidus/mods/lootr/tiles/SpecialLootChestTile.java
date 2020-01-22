package noobanidus.mods.lootr.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.data.BooleanData;
import noobanidus.mods.lootr.init.ModTiles;

import javax.annotation.Nullable;

public class SpecialLootChestTile extends ChestTileEntity {
  private ResourceLocation lootTable = null;
  private long seed = -1;

  public SpecialLootChestTile() {
    super(ModTiles.SPECIAL_LOOT_CHEST);
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    super.setLootTable(lootTableIn, seedIn);
    this.lootTable = lootTableIn;
    this.seed = seedIn;
  }

  private boolean isSpecialLootChest() {
    return lootTable != null;
  }

  @Override
  protected boolean checkLootAndRead(CompoundNBT compound) {
    if (isSpecialLootChest()) {
      return true;
    }
    return super.checkLootAndRead(compound);
  }

  @Override
  protected boolean checkLootAndWrite(CompoundNBT compound) {
    if (isSpecialLootChest()) {
      return true;
    }
    return super.checkLootAndWrite(compound);
  }

  @Override
  public void fillWithLoot(@Nullable PlayerEntity player) {
    // TODO: Override
  }

  public void fillWithLoot(PlayerEntity player, IInventory inventory) {
    if (this.world != null && this.lootTable != null && this.world.getServer() != null) {
      LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
      LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).withParameter(LootParameters.POSITION, new BlockPos(this.pos)).withSeed(this.seed);
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
      }

      loottable.fillInventory(inventory, builder.build(LootParameterSets.CHEST));
    }
  }

  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    if (compound.contains("specialLootChest_table", Constants.NBT.TAG_STRING)) {
      lootTable = new ResourceLocation(compound.getString("specialLootChest_table"));
    }
    if (compound.contains("specialLootChest_seed", Constants.NBT.TAG_LONG)) {
      seed = compound.getLong("specialLootChest_seed");
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound = super.write(compound);
    if (isSpecialLootChest()) {
      compound.putString("specialLootChest_table", lootTable.toString());
      compound.putLong("specialLootChest_seed", seed);
    }
    return compound;
  }

  // Specifically disabled to prevent weird interactions
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (!isSpecialLootChest()) {
      return super.getCapability(cap, side);
    }

    return LazyOptional.empty();
  }
}
