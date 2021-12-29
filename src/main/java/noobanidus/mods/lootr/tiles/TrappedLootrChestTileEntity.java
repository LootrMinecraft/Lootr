package noobanidus.mods.lootr.tiles;

import noobanidus.mods.lootr.init.ModTiles;

@SuppressWarnings("ConstantConditions")
public class TrappedLootrChestTileEntity extends LootrChestTileEntity {
  public TrappedLootrChestTileEntity() {
    super(ModTiles.TRAPPED_LOOT_CHEST);
  }

  @Override
  protected void signalOpenCount() {
    super.signalOpenCount();
    this.level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
  }
}
