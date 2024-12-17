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
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

import java.util.HashMap;
import java.util.Map;

public class Replacements {
  // TODO: This needs to be cleared whenever tags are refreshed
  private static Map<Block, Block> replacements = null;

  public static void clearReplacements() {
    replacements = null;
  }

  public static BlockState replacement(BlockState original) {
    if (original.is(LootrTags.Blocks.CONVERT_BLACKLIST)) {
      return null;
    }

    if (original.is(LootrTags.Blocks.CONTAINERS)) {
      return null;
    }

    if (replacements == null) {
      replacements = new HashMap<>();
    }

    if (replacements.get(original.getBlock()) == null && original.is(LootrTags.Blocks.CONVERT_BLOCK)) {
      if (original.getBlock() instanceof EntityBlock entityBlock) {
        BlockEntity be = entityBlock.newBlockEntity(BlockPos.ZERO, original);
        if (be instanceof RandomizableContainerBlockEntity) {
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
      BlockState state = replacement.defaultBlockState();
      for (Property<?> prop : original.getProperties()) {
        if (state.hasProperty(prop)) {
          state = safeReplace(state, original, prop);
        }
      }
      return state;
    }

    return null;
  }

  private static <V extends Comparable<V>> BlockState safeReplace(BlockState state, BlockState original, Property<V> property) {
    if (property == ChestBlock.TYPE && state.hasProperty(property)) {
      return state.setValue(ChestBlock.TYPE, ChestType.SINGLE);
    }
    if (original.hasProperty(property) && state.hasProperty(property)) {
      return state.setValue(property, original.getValue(property));
    }
    return state;
  }
}
