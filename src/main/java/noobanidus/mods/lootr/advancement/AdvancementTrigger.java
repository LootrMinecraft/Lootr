package noobanidus.mods.lootr.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class AdvancementTrigger extends SimpleCriterionTrigger<AdvancementTrigger.TriggerInstance> {
  @Override
  public Codec<AdvancementTrigger.TriggerInstance> codec() {
    return TriggerInstance.CODEC;
  }

  public void trigger(ServerPlayer player, ResourceLocation advancementId) {
    this.trigger(player, (instance) -> instance.test(advancementId));
  }

  public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                Optional<ResourceLocation> advancement) implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<AdvancementTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(codec -> codec.group(ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ResourceLocation.CODEC.optionalFieldOf("advancement").forGetter(TriggerInstance::advancement)).apply(codec, AdvancementTrigger.TriggerInstance::new));

    public boolean test(ResourceLocation advancementId) {
      return this.advancement.isEmpty() || this.advancement.get().equals(advancementId);
    }
  }
}