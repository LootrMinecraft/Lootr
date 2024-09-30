package noobanidus.mods.lootr.common.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.LockCode;

public record DataToCopy(CompoundTag data, LockCode lockCode) {
  public static final DataToCopy EMPTY = new DataToCopy(new CompoundTag(), LockCode.NO_LOCK);
}
