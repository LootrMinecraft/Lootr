package noobanidus.mods.lootr.common.impl.type;

import net.minecraft.world.entity.EntityType;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import noobanidus.mods.lootr.common.api.filler.DefaultBrushableLootFiller;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;
import org.jetbrains.annotations.Nullable;

public abstract class BrushableLootrType implements ILootrType {
  @Override
  public @Nullable EntityType<?> getReplacementEntity() {
    return null;
  }

  @Override
  public ILootFiller getDefaultFiller() {
    return DefaultBrushableLootFiller.getInstance();
  }

  @Override
  public boolean canBeMarkedUnopened() {
    return false;
  }

  @Override
  public boolean canDropContentsWhenBroken() {
    return false;
  }

  @Override
  public boolean canDecay() {
    return false;
  }

  @Override
  public boolean canRefresh() {
    return false;
  }

  @Override
  public boolean displaysUnopenedParticle() {
    return false;
  }
}
