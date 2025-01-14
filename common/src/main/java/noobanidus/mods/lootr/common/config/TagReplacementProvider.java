package noobanidus.mods.lootr.common.config;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.IReplacementProvider;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class TagReplacementProvider implements IReplacementProvider {
  private final TagKey<Block> blockTagKey;
  private final Supplier<Block> replacementBlock;

  public static final TagReplacementProvider CHEST = new TagReplacementProvider(LootrTags.Blocks.CONVERT_CHESTS, LootrRegistry::getChestBlock);
  public static final TagReplacementProvider TRAPPED_CHEST = new TagReplacementProvider(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS, LootrRegistry::getTrappedChestBlock);
  public static final TagReplacementProvider BARREL = new TagReplacementProvider(LootrTags.Blocks.CONVERT_BARRELS, LootrRegistry::getBarrelBlock);
  public static final TagReplacementProvider SHULKER_BOX = new TagReplacementProvider(LootrTags.Blocks.CONVERT_SHULKERS, LootrRegistry::getShulkerBlock);

  public TagReplacementProvider(TagKey<Block> blockTagKey, Supplier<Block> blockSupplier) {
    this.blockTagKey = blockTagKey;
    this.replacementBlock = blockSupplier;
  }

  @Override
  public @Nullable Block replacementBlockFor(Block original, BlockEntity blockEntity) {
    if (original.builtInRegistryHolder().is(blockTagKey)) {
      return replacementBlock.get();
    }

    return null;
  }

  @Override
  public @Nullable StateMapper stateMapperForBlock(Block original) {
    if (original.builtInRegistryHolder().is(blockTagKey)) {
      return DefaultStateMapper.INSTANCE;
    }
    return null;
  }

}
