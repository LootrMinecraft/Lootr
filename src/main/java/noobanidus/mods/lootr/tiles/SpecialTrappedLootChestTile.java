package noobanidus.mods.lootr.tiles;

import noobanidus.mods.lootr.init.ModTiles;

public class SpecialTrappedLootChestTile extends SpecialLootChestTile {
  public SpecialTrappedLootChestTile() {
    super(ModTiles.SPECIAL_TRAPPED_LOOT_CHEST);
  }

  @Override
  protected void onOpenOrClose() {
    super.onOpenOrClose();
    this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockState().getBlock());
  }
}
