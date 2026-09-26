package noobanidus.mods.lootr.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

public record AlwaysTriggerInstance(
    Optional<Holder<LootItemCondition>> player) implements SimpleCriterionTrigger.SimpleInstance {
  public static final Codec<AlwaysTriggerInstance> CODEC = RecordCodecBuilder.create(codec -> codec.group(LootItemCondition.CODEC.optionalFieldOf("player")
      .forGetter(AlwaysTriggerInstance::player)).apply(codec, AlwaysTriggerInstance::new));

  public boolean test() {
    return true;
  }
}
