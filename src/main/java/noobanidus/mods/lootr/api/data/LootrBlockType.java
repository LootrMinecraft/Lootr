package noobanidus.mods.lootr.api.data;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum LootrBlockType {
  CHEST(Blocks.CHEST),
  TRAPPED_CHEST(Blocks.TRAPPED_CHEST),
  BARREL(Blocks.BARREL),
  SHULKER(Blocks.SHULKER_BOX),
  INVENTORY(Blocks.CHEST),
  ENTITY(Blocks.AIR);

  private final Block block;

  LootrBlockType(Block block) {
    this.block = block;
  }

  public Block getBlock () {
    return block;
  }
}
