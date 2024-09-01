package noobanidus.mods.lootr.common.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.LockCode;

// TODO: I can't think of a better place for this
public record DataToCopy(CompoundTag data, LockCode lockCode) {
  public static final DataToCopy EMPTY = new DataToCopy(new CompoundTag(), LockCode.NO_LOCK);
}
