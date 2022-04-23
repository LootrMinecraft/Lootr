package noobanidus.mods.lootr.block.tile;

@SuppressWarnings("ConstantConditions")
public class TrappedLootrChestTileEntity extends LootrChestTileEntity {
  public TrappedLootrChestTileEntity() {
    super();
  }

  @Override
  protected void signalOpenCount() {
    super.signalOpenCount();
    this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), true);
  }
}
