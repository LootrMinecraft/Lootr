package noobanidus.mods.lootr.common.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.advancement.ITrapdoorTrigger;
import noobanidus.mods.lootr.common.api.advancement.ITrigger;

import java.util.Optional;

public class TrapdoorTrigger extends SimpleCriterionTrigger<AlwaysTriggerInstance> implements ITrapdoorTrigger {
  @Override
  public void trigger(ServerPlayer player) {
    this.trigger(player, AlwaysTriggerInstance::test);
  }

  @Override
  public Codec<AlwaysTriggerInstance> codec() {
    return AlwaysTriggerInstance.CODEC;
  }

  public static Criterion<AlwaysTriggerInstance> trapdoor(ITrigger trigger) {
    return ((TrapdoorTrigger) trigger.getTrigger()).createCriterion(new
        AlwaysTriggerInstance(Optional.empty()));
  }
}
