package noobanidus.mods.lootr.common.api.integration.decorated;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.PotDecorations;

import java.util.List;
import java.util.stream.Stream;

public record PotDecorationsAdapter(ItemStack back, ItemStack left, ItemStack right, ItemStack front) {
  public static final PotDecorationsAdapter EMPTY = new PotDecorationsAdapter(PotDecorations.EMPTY);

  public static final Codec<PotDecorationsAdapter> CODEC = ItemStack.CODEC
      .sizeLimitedListOf(4)
      .xmap(PotDecorationsAdapter::new, PotDecorationsAdapter::ordered);

  public static final StreamCodec<RegistryFriendlyByteBuf, PotDecorationsAdapter> STREAM_CODEC = ItemStack.STREAM_CODEC
      .apply(ByteBufCodecs.list(4))
      .map(PotDecorationsAdapter::new, PotDecorationsAdapter::ordered);

  public PotDecorationsAdapter(PotDecorationsAdapter decorations) {
    this(decorations.back().copy(), decorations.left().copy(), decorations.right().copy(), decorations.front().copy());
  }

  public PotDecorationsAdapter(PotDecorations decorations) {
    this(decorations.back().map(ItemStack::new).orElse(ItemStack.EMPTY),
        decorations.left().map(ItemStack::new).orElse(ItemStack.EMPTY),
        decorations.right().map(ItemStack::new).orElse(ItemStack.EMPTY),
        decorations.front().map(ItemStack::new).orElse(ItemStack.EMPTY));
  }

  public PotDecorationsAdapter(List<ItemStack> itemStacks) {
    this(itemStacks.get(0), itemStacks.get(1), itemStacks.get(2), itemStacks.get(3));
  }

  public List<ItemStack> ordered() {
    return Stream.of(back, left, right, front).map(o -> o.isEmpty() ? new ItemStack(Items.BRICK) : o).toList();
  }
}
