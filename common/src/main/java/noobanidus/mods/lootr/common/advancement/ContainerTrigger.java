package noobanidus.mods.lootr.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.advancement.ITrigger;

import java.util.Optional;
import java.util.UUID;

public class ContainerTrigger extends SimpleCriterionTrigger<ContainerTrigger.TriggerInstance> implements IContainerTrigger {
  public void trigger(ServerPlayer player, UUID condition) {
    this.trigger(player, (instance) -> instance.test(player, condition));
  }

  @Override
  public Codec<TriggerInstance> codec() {
    return TriggerInstance.CODEC;
  }

  public static Criterion<ContainerTrigger.TriggerInstance> looted(ITrigger trigger) {
    return ((ContainerTrigger) trigger.getTrigger()).createCriterion(new
       TriggerInstance(Optional.empty()));
  }

  public record TriggerInstance(
      Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(codec -> codec.group(ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)).apply(codec, TriggerInstance::new));

    public boolean test(ServerPlayer player, UUID container) {
      if (LootrAPI.isAwarded(container, player)) {
        return false;
      } else {
        LootrAPI.award(container, player);
        return true;
      }
    }
  }
}
