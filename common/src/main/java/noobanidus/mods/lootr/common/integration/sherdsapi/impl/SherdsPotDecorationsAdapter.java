package noobanidus.mods.lootr.common.integration.sherdsapi.impl;

import dev.thomasglasser.sherdsapi.impl.StackPotDecorations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.client.IPotDecorationsAdapter;
import org.apache.commons.lang3.NotImplementedException;

public class SherdsPotDecorationsAdapter implements IPotDecorationsAdapter {
  private final ItemStack back;
  private final ItemStack front;
  private final ItemStack right;
  private final ItemStack left;

  public SherdsPotDecorationsAdapter(StackPotDecorations decorations) {
    this.back = decorations.back().orElse(ItemStack.EMPTY);
    this.front = decorations.front().orElse(ItemStack.EMPTY);
    this.right = decorations.right().orElse(ItemStack.EMPTY);
    this.left = decorations.left().orElse(ItemStack.EMPTY);
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
  public CompoundTag save(CompoundTag tag) {
    throw new NotImplementedException("This function intentionally left blank.");
  }

  @Override
  public void load(CompoundTag tag) {
    throw new NotImplementedException("This function intentionally left blank.");
  }
}
