package noobanidus.mods.lootr.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.interfaces.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.interfaces.advancement.ITrigger;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public class ContainerTrigger extends SimpleCriterionTrigger<ContainerTrigger.TriggerInstance> implements IContainerTrigger {
  @Override
  public void trigger(ServerPlayer player, UUID condition) {
    this.trigger(player, TriggerInstance::test);
  }

  @Override
  public @NonNull Codec<TriggerInstance> codec() {
    return TriggerInstance.CODEC;
  }

  public static Criterion<ContainerTrigger.TriggerInstance> looted(ITrigger trigger) {
    return ((ContainerTrigger) trigger.getTrigger()).createCriterion(new
       TriggerInstance(Optional.empty()));
  }

  public record TriggerInstance(
      Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(codec -> codec.group(ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)).apply(codec, TriggerInstance::new));

    public boolean test() {
      return true;
    }
  }
}
