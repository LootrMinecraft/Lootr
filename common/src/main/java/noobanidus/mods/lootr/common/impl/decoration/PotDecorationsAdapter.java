package noobanidus.mods.lootr.common.impl.decoration;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.PotDecorations;
import noobanidus.mods.lootr.common.api.client.IPotDecorationsAdapter;

import java.util.List;

public class PotDecorationsAdapter implements IPotDecorationsAdapter {
  public static final PotDecorationsAdapter EMPTY = new PotDecorationsAdapter(PotDecorations.EMPTY);

  public static final Codec<IPotDecorationsAdapter> CODEC = ItemStack.CODEC
      .sizeLimitedListOf(4)
      .xmap(PotDecorationsAdapter::new, IPotDecorationsAdapter::ordered);

  private ItemStack back;
  private ItemStack front;
  private ItemStack right;
  private ItemStack left;

  public PotDecorationsAdapter(IPotDecorationsAdapter decorations) {
    this.back = decorations.back();
    this.left = decorations.left();
    this.right = decorations.right();
    this.front = decorations.front();
  }

  public PotDecorationsAdapter(PotDecorations decorations) {
    this.back = decorations.back().map(ItemStack::new).orElse(ItemStack.EMPTY);
    this.left = decorations.left().map(ItemStack::new).orElse(ItemStack.EMPTY);
    this.right = decorations.right().map(ItemStack::new).orElse(ItemStack.EMPTY);
    this.front = decorations.front().map(ItemStack::new).orElse(ItemStack.EMPTY);
  }

  public PotDecorationsAdapter(List<ItemStack> itemStacks) {
    if (itemStacks.size() != 4) {
      throw new IllegalArgumentException("Expected exactly 4 item stacks, got " + itemStacks.size());
    }
    this.back = itemStacks.get(0);
    this.left = itemStacks.get(1);
    this.right = itemStacks.get(2);
    this.front = itemStacks.get(3);
  }

  @Override
  public ItemStack back() {
    return back;
  }

  @Override
  public ItemStack front() {
    return front;
  }

  @Override
  public ItemStack right() {
    return right;
  }

  @Override
  public ItemStack left() {
    return left;
  }

  @Override
  public void load(CompoundTag tag) {
    if (!tag.contains("decorations")) {
      return;
    }

    // TODO: Shim in old types
    IPotDecorationsAdapter decoded = CODEC.decode(NbtOps.INSTANCE, tag.get("decorations")).getOrThrow().getFirst();
    this.back = decoded.back();
    this.front = decoded.front();
    this.right = decoded.right();
    this.left = decoded.left();
  }

  @Override
  public CompoundTag save(CompoundTag tag) {
    if (!this.isEmpty()) {
      tag.put("decorations", CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
    }
    return tag;
  }
}
