package noobanidus.mods.lootr.common.api.helper;

import com.google.common.base.Suppliers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import noobanidus.mods.lootr.common.api.NBTConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SimpleLootrEntityInstance extends SimpleLootrInstance{
  private final Supplier<UUID> uuidSupplier;
  private final Entity entityInstance;
  private final EntityDataAccessor<Boolean> refreshing, decaying;

  public SimpleLootrEntityInstance(Entity entityInstance, Supplier<Set<UUID>> visualOpenersSupplier, int size, EntityDataAccessor<Boolean> refreshing, EntityDataAccessor<Boolean> decaying) {
    super(visualOpenersSupplier, size);
    this.providesOwnUuid = true;
    this.uuidSupplier = Suppliers.memoize(entityInstance::getUUID);
    this.refreshing = refreshing;
    this.decaying = decaying;
    this.entityInstance = entityInstance;
  }

  @Override
  public @NotNull UUID getId() {
    return uuidSupplier.get();
  }

  @Override
  public void setClientRefreshing(boolean value) {
    entityInstance.getEntityData().set(refreshing, value);
  }

  @Override
  public void setClientDecaying(boolean value) {
    entityInstance.getEntityData().set(decaying, value);
  }

  @Override
  public boolean isClientRefreshing() {
    return entityInstance.getEntityData().get(refreshing);
  }

  @Override
  public boolean isClientDecaying() {
    return entityInstance.getEntityData().get(decaying);
  }

  @Override
  public void saveAdditional(ValueOutput output, boolean isClientSide) {
    super.saveAdditional(output, isClientSide);
    output.putBoolean(NBTConstants.CLIENT_REFRESHING, entityInstance.getEntityData().get(refreshing));
    output.putBoolean(NBTConstants.CLIENT_DECAYING, entityInstance.getEntityData().get(decaying));
  }

  @Override
  public void loadAdditional(ValueInput input) {
    var data = entityInstance.getEntityData();
    data.set(decaying, input.getBooleanOr(NBTConstants.CLIENT_DECAYING, false));
    data.set(refreshing, input.getBooleanOr(NBTConstants.CLIENT_REFRESHING, false));
    super.loadAdditional(input);
  }
}