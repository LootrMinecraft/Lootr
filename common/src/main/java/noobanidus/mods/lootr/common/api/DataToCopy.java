package noobanidus.mods.lootr.common.api;

import net.minecraft.nbt.CompoundTag;

// TODO: I can't think of a better place for this
public record DataToCopy(CompoundTag data) {
  public static final DataToCopy EMPTY = new DataToCopy(new CompoundTag());
}
