package noobanidus.mods.lootr.common.api.wrapper;

import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

import java.util.function.Function;

/**
 * Wraps an object (presumably a block entity) as an ILootrBlockEntity.
 * <br />
 * Wrappers are loaded via services. Specifically, the class implementing this
 * wrapper should be listed (fully qualified name) in a file located at:
 * META-INF/services/noobanidus.mods.lootr.common.api.ILootrBlockEntityWrapper
 * <br />
 * These wrappers are then used to resolve block entities into ILootrBlockEntity,
 * rather than using specific "instanceof" checks or casts.
 * <br />
 * While the default implementations of this (i.e., LootrBarrelBlockEntity$DefaultBlockEntityWrapper)
 * always return themselves (thus meaning that the output of `apply` is the same
 * as the input), this is not a requirement and there may be no relationship between
 * them.
 * <br />
 * These are specifically for instances where you want to optionally support Lootr
 * if it is also installed, but otherwise want to function as a normal block entity
 * with Vanilla-esque (i.e., single loot generation) when it is not installed.
 **/
public interface ILootrBlockEntityWrapper<T> extends Function<T, ILootrBlockEntity> {
  @Override
  ILootrBlockEntity apply (T blockEntity);

  BlockEntityType<?> getBlockEntityType ();
}
