package noobanidus.mods.lootr.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.advancement.IAdvancementTrigger;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

import java.util.Optional;

public class AdvancementTrigger extends SimpleCriterionTrigger<AdvancementTrigger.TriggerInstance> implements IAdvancementTrigger {
  @Override
  public Codec<AdvancementTrigger.TriggerInstance> codec() {
    return TriggerInstance.CODEC;
  }

  @Override
  public void trigger(ServerPlayer player, Identifier advancementId) {
    this.trigger(player, (instance) -> instance.test(advancementId));
  }

  public static Criterion<TriggerInstance> completed(Identifier advancementId) {
    return ((AdvancementTrigger) LootrRegistry.getAdvancementTrigger()).createCriterion(new
       TriggerInstance(Optional.empty(), Optional.of(advancementId)));
  }

  public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                Optional<Identifier> advancement) implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<AdvancementTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(codec -> codec.group(ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), Identifier.CODEC.optionalFieldOf("advancement").forGetter(TriggerInstance::advancement)).apply(codec, AdvancementTrigger.TriggerInstance::new));

    public boolean test(Identifier advancementId) {
      return this.advancement.isEmpty() || this.advancement.get().equals(advancementId);
    }
  }
}
