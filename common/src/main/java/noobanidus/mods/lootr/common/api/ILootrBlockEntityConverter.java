package noobanidus.mods.lootr.common.api;

import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

import java.util.function.Function;

/**
 * Converts an object (of any type) into an ILootrBlockEntity.
 * <br />
 * Converters are loaded via services. Specifically, the class implementing this
 * converter should be listed (fully qualified name) in a file located at:
 * META-INF/services/noobanidus.mods.lootr.common.api.ILootrBlockEntityConverter
 * <br />
 * These converters are then used to resolve block entities into ILootrBlockEntity,
 * rather than using specific "instanceof" checks or casts.
 * <br />
 * While the default implementations of this (i.e., LootrBarrelBlockEntity$DefaultBlockEntityConverter)
 * always return themselves (thus meaning that the output of `apply` is the same
 * as the input), this is not a requirement and there may be no relationship between
 * them.
 **/
public interface ILootrBlockEntityConverter<T> extends Function<T, ILootrBlockEntity> {
  @Override
  ILootrBlockEntity apply (T blockEntity);

  BlockEntityType<?> getBlockEntityType ();
}
