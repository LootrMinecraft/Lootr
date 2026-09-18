package noobanidus.mods.lootr.common.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.interfaces.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.interfaces.advancement.ITrigger;

import java.util.Optional;
import java.util.UUID;

public class ContainerTrigger extends SimpleCriterionTrigger<AlwaysTriggerInstance> implements IContainerTrigger {
  @Override
  public void trigger(ServerPlayer player, UUID condition) {
    this.trigger(player, AlwaysTriggerInstance::test);
  }

  @Override
  public Codec<AlwaysTriggerInstance> codec() {
    return AlwaysTriggerInstance.CODEC;
  }

  public static Criterion<AlwaysTriggerInstance> looted(ITrigger trigger) {
    return ((ContainerTrigger) trigger.getTrigger()).createCriterion(new
        AlwaysTriggerInstance(Optional.empty()));
  }
}
