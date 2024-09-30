package noobanidus.mods.lootr.common.api;

import net.minecraft.world.entity.EntityType;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;

import java.util.function.Function;

public interface ILootrEntityConverter<T> extends Function<T, ILootrCart> {
  @Override
  ILootrCart apply (T entity);

  EntityType<?> getEntityType ();
}
