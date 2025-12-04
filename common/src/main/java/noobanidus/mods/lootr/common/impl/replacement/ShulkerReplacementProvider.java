package noobanidus.mods.lootr.common.impl.replacement;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.api.replacement.ILootrBlockReplacementProvider;

public class ShulkerReplacementProvider implements ILootrBlockReplacementProvider {
  @Override
  public TagKey<Block> getApplicableTag() {
    return LootrTags.Blocks.CONVERT_SHULKERS;
  }

  @Override
  public Block getBlock() {
    return LootrRegistry.getShulkerBlock();
  }
}
