package noobanidus.mods.lootr.common.api;

import net.minecraft.world.level.block.state.BlockState;

public enum LootrChestType {
  NORMAL,
  TRAPPED,
  COPPER,
  WEATHERED,
  OXIDIZED,
  EXPOSED;

  public static LootrChestType fromState (BlockState state) {
    if (state.is(LootrTags.Blocks.TRAPPED_CHESTS)) {
      return TRAPPED;
    } else if (state.is(LootrTags.Blocks.COPPER_CHESTS)) {
      return COPPER;
    } else if (state.is(LootrTags.Blocks.WEATHERED_COPPER_CHESTS)) {
      return WEATHERED;
    } else if (state.is(LootrTags.Blocks.OXIDIZED_COPPER_CHESTS)) {
      return OXIDIZED;
    } else if (state.is(LootrTags.Blocks.EXPOSED_COPPER_CHESTS)) {
      return EXPOSED;
    } else {
      return NORMAL;
    }
  }
}
