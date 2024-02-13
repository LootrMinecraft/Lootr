package net.zestyblaze.lootr.advancement;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.zestyblaze.lootr.registry.LootrStatsInit;

import java.util.Optional;

public class LootedStatTrigger extends SimpleCriterionTrigger<LootedStatTrigger.TriggerInstance> {
    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> instance.test(player));
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> player,
                                         MinMaxBounds.Ints score) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(codec -> codec.group(ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(LootedStatTrigger.TriggerInstance::player), ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "score", MinMaxBounds.Ints.ANY).forGetter(LootedStatTrigger.TriggerInstance::score)).apply(codec, LootedStatTrigger.TriggerInstance::new));

        public boolean test (ServerPlayer player) {
            return this.score.matches(player.getStats().getValue(LootrStatsInit.LOOTED_STAT));
        }
    }
}

