package noobanidus.mods.lootr.common.impl.replacement;

import com.google.auto.service.AutoService;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.api.replacement.ILootrBlockReplacementProvider;

@AutoService(ILootrBlockReplacementProvider.class)
public class GravelReplacementProvider implements ILootrBlockReplacementProvider{
  @Override
  public TagKey<Block> getApplicableTag() {
    return LootrTags.Blocks.CONVERT_GRAVELS;
  }

  @Override
  public Block getBlock() {
    return LootrRegistry.getSuspiciousGravel();
  }
}
