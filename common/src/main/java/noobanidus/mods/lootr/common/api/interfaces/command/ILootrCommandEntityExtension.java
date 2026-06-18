package noobanidus.mods.lootr.common.api.interfaces.command;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface ILootrCommandEntityExtension<T extends Entity> extends ILootrCommandExtension {
  EntityType<T> getType();
  default void process (T entity) {
    // NO-OP
  }
}
