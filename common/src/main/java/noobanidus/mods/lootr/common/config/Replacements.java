package noobanidus.mods.lootr.common.config;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import noobanidus.mods.lootr.common.api.IReplacementProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Replacements {
  // TODO: This needs to be cleared whenever tags are refreshed
  private static Set<Block> ignore = null;
  private static Map<Block, Block> replacements = null;
  private static Map<Block, IReplacementProvider.StateMapper> stateMappers = null;

  public static IReplacementProvider DEFAULT_TAGGED_PROVIDER = new IReplacementProvider() {
    @Override
    public Block replacementBlockFor(Block original, BlockEntity blockEntity) {
      return replacement(original).getBlock();
    }

    @Override
    public IReplacementProvider.StateMapper stateMapperForBlock(Block original) {
      return (originalState, blockEntity) -> replacement(originalState);
    }
  };

  public static void clearReplacements() {
    replacements = null;
    ignore = null;
    stateMappers = null;
  }

  public static BlockState replacement(BlockState original) {
    if (original.is(LootrTags.Blocks.CONVERT_BLACKLIST)) {
      return null;
    }

    if (original.is(LootrTags.Blocks.CONTAINERS)) {
      return null;
    }

    if (ignore == null) {
      ignore = new HashSet<>();
    }

    if (ignore.contains(original.getBlock())) {
      return null;
    }

    if (replacements == null) {
      replacements = new HashMap<>();
    }

    // We only support blocks tagged as `CONVERT_BLOCK`
    if (replacements.get(original.getBlock()) == null && original.is(LootrTags.Blocks.CONVERT_BLOCK)) {
      // We only support EntityBlock derivatives
      if (original.getBlock() instanceof EntityBlock entityBlock) {
        BlockEntity be = entityBlock.newBlockEntity(BlockPos.ZERO, original);
        // If it already resolves to a block entity, skip it
        if (LootrAPI.resolveBlockEntity(be) != null) {
          ignore.add(original.getBlock());
        }
        // We only support block entities that have a converter
        if (LootrAPI.hasConverterForReplacement(be)) {


          // Default replacements, checking trapped chests before normal chests
          if (original.is(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS)) {
            replacements.put(original.getBlock(), LootrRegistry.getTrappedChestBlock());
          } else if (original.is(LootrTags.Blocks.CONVERT_BARRELS)) {
            replacements.put(original.getBlock(), LootrRegistry.getBarrelBlock());
          } else if (original.is(LootrTags.Blocks.CONVERT_CHESTS)) {
            replacements.put(original.getBlock(), LootrRegistry.getChestBlock());
          } else if (original.is(LootrTags.Blocks.CONVERT_SHULKERS)) {
            replacements.put(original.getBlock(), LootrRegistry.getShulkerBlock());
          }
        }
      }
    }

    Block replacement = replacements.get(original.getBlock());

    if (replacement != null) {
      BlockState state;
      IReplacementProvider.StateMapper mapper = stateMappers.get(replacement);
      if (mapper != null) {
        state = mapper.map(replacement.defaultBlockState(), original, null);
      } else {
        state = DefaultStateMapper.INSTANCE.map(replacement.defaultBlockState(), original, null);
      }
      return state;
    }

    ignore.add(original.getBlock());

    return null;
  }

  public static <V extends Comparable<V>> BlockState safeReplace(BlockState state, BlockState original, Property<V> property) {
    if (property == ChestBlock.TYPE && state.hasProperty(property)) {
      return state.setValue(ChestBlock.TYPE, ChestType.SINGLE);
    }
    if (original.hasProperty(property) && state.hasProperty(property)) {
      return state.setValue(property, original.getValue(property));
    }
    return state;
  }
}
