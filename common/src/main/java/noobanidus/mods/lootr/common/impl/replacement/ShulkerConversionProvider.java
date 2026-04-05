package noobanidus.mods.lootr.common.impl.replacement;

import com.google.auto.service.AutoService;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.api.conversion.ILootrBlockConversionProvider;

@AutoService(ILootrBlockConversionProvider.class)
public class ShulkerConversionProvider implements ILootrBlockConversionProvider {
  @Override
  public TagKey<Block> getApplicableTag() {
    return LootrTags.Blocks.CONVERT_SHULKERS;
  }

  @Override
  public Block getBlock() {
    return LootrRegistry.getShulkerBlock();
  }
}
