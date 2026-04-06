package noobanidus.mods.lootr.common.impl.conversion;

import com.google.auto.service.AutoService;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.conversion.ILootrBlockConversionProvider;

@AutoService(ILootrBlockConversionProvider.class)
public class ChestConversionProvider implements ILootrBlockConversionProvider {
  @Override
  public int getPriority() {
    return -10;
  }

  @Override
  public TagKey<Block> getApplicableTag() {
    return LootrTags.Blocks.CONVERT_CHESTS;
  }

  @Override
  public Block getBlock() {
    return LootrRegistry.getChestBlock();
  }
}
