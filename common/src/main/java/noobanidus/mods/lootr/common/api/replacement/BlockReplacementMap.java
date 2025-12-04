package noobanidus.mods.lootr.common.api.replacement;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BlockReplacementMap {
  private final Set<Block> ignoredBlocks = new HashSet<>();
  private final Map<Block, Block> replacements = new Object2ObjectOpenHashMap<>();
  private final List<ILootrBlockReplacementProvider> allProviders = new ArrayList<>();

  public void register(ILootrBlockReplacementProvider provider) {
    allProviders.add(provider);
  }

  public void sort() {
    allProviders.sort(Comparator.comparingInt(ILootrBlockReplacementProvider::getPriority).reversed());
  }

  public void clear() {
    ignoredBlocks.clear();
    replacements.clear();
  }

  @Nullable
  public BlockState getReplacement(BlockState state) {
    Block block = state.getBlock();
    if (ignoredBlocks.contains(block)) {
      return null;
    }

    Block result = replacements.get(block);

    if (result == null) {
      for (ILootrBlockReplacementProvider provider : allProviders) {
        Block replacement = provider.apply(block);
        if (replacement != null) {
          result = replacement;
          break;
        }
      }
    }

    if (result == null) {
      ignoredBlocks.add(block);
      return null;
    }


    BlockState replacement = result.defaultBlockState();
    for (Property<?> prop : replacement.getProperties()) {
      if (state.hasProperty(prop)) {
        replacement = safeReplace(replacement, state, prop);
      }
    }
    return replacement;
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
