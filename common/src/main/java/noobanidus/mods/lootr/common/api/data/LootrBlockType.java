package noobanidus.mods.lootr.common.api.data;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * TODO: This actually needs to be migrated, either to a custom
 * registry or some other method in order to allow add-on mods to
 * create custom containers that have replacements.
 */
@Deprecated
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
