package noobanidus.mods.lootr.tiles;

import noobanidus.mods.lootr.init.ModTiles;

@SuppressWarnings("ConstantConditions")
public class SpecialTrappedLootChestTile extends SpecialLootChestTile {
  public SpecialTrappedLootChestTile() {
    super(ModTiles.SPECIAL_TRAPPED_LOOT_CHEST);
  }

  @Override
  protected void signalOpenCount() {
    super.signalOpenCount();
    this.level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
  }
}
