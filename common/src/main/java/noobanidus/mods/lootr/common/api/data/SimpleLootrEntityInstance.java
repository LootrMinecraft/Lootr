package noobanidus.mods.lootr.common.api.data;

import com.google.common.base.Suppliers;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import noobanidus.mods.lootr.common.api.NBTConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SimpleLootrEntityInstance extends SimpleLootrInstance{
  private final Supplier<UUID> uuidSupplier;
  private final EntityDataAccessor<Boolean> refreshing, decaying;
  private final Entity entityInstance;

  public SimpleLootrEntityInstance(Entity entityInstance, Supplier<Set<UUID>> visualOpenersSupplier, int size, EntityDataAccessor<Boolean> refreshing, EntityDataAccessor<Boolean> decaying) {
    super(visualOpenersSupplier, size);
    this.providesOwnUuid = true;
    this.uuidSupplier = Suppliers.memoize(entityInstance::getUUID);
    this.refreshing = refreshing;
    this.decaying = decaying;
    this.entityInstance = entityInstance;
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return uuidSupplier.get();
  }

  @Override
  public boolean isClientRefreshing() {
    return entityInstance.getEntityData().get(refreshing);
  }

  @Override
  public void setClientRefreshing(boolean value) {
    entityInstance.getEntityData().set(refreshing, value);
  }

  @Override
  public boolean isClientDecaying() {
    return entityInstance.getEntityData().get(decaying);
  }

  @Override
  public void setClientDecaying(boolean value) {
    entityInstance.getEntityData().set(decaying, value);
  }

  @Override
  public void loadAdditional(CompoundTag compound, HolderLookup.Provider provder) {
    if (compound.contains(NBTConstants.CLIENT_DECAYING)) {
      entityInstance.getEntityData().set(decaying, compound.getBoolean(NBTConstants.CLIENT_DECAYING));
    }
    if (compound.contains(NBTConstants.CLIENT_REFRESHING)) {
      entityInstance.getEntityData().set(refreshing, compound.getBoolean(NBTConstants.CLIENT_REFRESHING));
    }
    super.loadAdditional(compound, provder);
  }

  @Override
  public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider, boolean isClientSide) {
    super.saveAdditional(compound, provider, isClientSide);
    compound.putBoolean(NBTConstants.CLIENT_REFRESHING, entityInstance.getEntityData().get(refreshing));
    compound.putBoolean(NBTConstants.CLIENT_DECAYING, entityInstance.getEntityData().get(decaying));
  }
}
