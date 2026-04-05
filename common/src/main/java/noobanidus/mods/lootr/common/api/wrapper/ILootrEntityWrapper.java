package noobanidus.mods.lootr.common.api.wrapper;

import net.minecraft.world.entity.EntityType;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;

import java.util.function.Function;

/**
 * Wraps an object (of any type) into an ILootrEntity.
 * <br />
 * Wrappers are loaded via services. Specifically, the class implementing this
 * wrapper should be listed (fully qualified name) in a file located at:
 * META-INF/services/noobanidus.mods.lootr.common.api.ILootrEntityWraper
 * <br />
 * These wrappers are then used to resolve entities into ILootrEntity
 * rather than using specific "instanceof" checks or casts.
 * <br />
 * While the default implementations of this (i.e., LootrChestMinecartEntity$DefaultEntityWraper)
 * returns itself (thus meaning that the output of `apply` is the same
 * as the input), this is not a requirement and there may be no relationship between
 * them.
 * <br />
 * These are specifically for instances where you want to optionally support Lootr
 * if it is also installed, but otherwise want to function as a normal block entity
 * with Vanilla-esque (i.e., single loot generation) when it is not installed.*
 **/
public interface ILootrEntityWrapper<T> extends Function<T, ILootrEntity> {
  @Override
  ILootrEntity apply(T entity);

  EntityType<?> getEntityType();
}
