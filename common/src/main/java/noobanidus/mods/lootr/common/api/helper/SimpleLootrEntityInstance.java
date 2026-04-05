package noobanidus.mods.lootr.common.api.helper;

import com.google.common.base.Suppliers;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SimpleLootrEntityInstance extends SimpleLootrInstance{
  private final Supplier<UUID> uuidSupplier;

  public SimpleLootrEntityInstance(Entity entityInstance, Supplier<Set<UUID>> visualOpenersSupplier, int size) {
    super(visualOpenersSupplier, size);
    this.providesOwnUuid = true;
    this.uuidSupplier = Suppliers.memoize(entityInstance::getUUID);
  }

  @Override
  public @NotNull UUID getId() {
    return uuidSupplier.get();
  }
}
