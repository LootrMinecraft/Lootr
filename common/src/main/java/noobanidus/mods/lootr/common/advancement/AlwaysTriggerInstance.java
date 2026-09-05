package noobanidus.mods.lootr.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;

import java.util.Optional;

public record AlwaysTriggerInstance(
    Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
  public static final Codec<AlwaysTriggerInstance> CODEC = RecordCodecBuilder.create(codec -> codec.group(ContextAwarePredicate.CODEC.optionalFieldOf("player")
      .forGetter(AlwaysTriggerInstance::player)).apply(codec, AlwaysTriggerInstance::new));

  public boolean test() {
    return true;
  }
}
