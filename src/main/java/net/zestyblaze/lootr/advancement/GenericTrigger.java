package net.zestyblaze.lootr.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.zestyblaze.lootr.api.advancement.IGenericPredicate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericTrigger<T> implements Criterion<GenericTrigger.Instance<T>> {
    private final Identifier id;
    private final Map<PlayerAdvancementTracker, Listeners<T>> listeners = Maps.newHashMap();
    private final IGenericPredicate<T> predicate;

    public GenericTrigger(String id, IGenericPredicate<T> predicate) {
        this(new Identifier(id), predicate);
    }

    public GenericTrigger(Identifier id, IGenericPredicate<T> predicate) {
        this.id = id;
        this.predicate = predicate;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void beginTrackingCondition(PlayerAdvancementTracker manager, ConditionsContainer<Instance<T>> conditions) {
        Listeners<T> list = listeners.get(manager);

        if(list == null) {
            list = new Listeners<>(manager);
            listeners.put(manager, list);
        }

        list.add(conditions);
    }

    @Override
    public void endTrackingCondition(PlayerAdvancementTracker manager, ConditionsContainer<Instance<T>> conditions) {
        Listeners<T> list = listeners.get(manager);

        if(list != null) {
            list.remove(conditions);

            if(list.isEmpty()) {
                listeners.remove(manager);
            }
        }
    }

    @Override
    public void endTracking(PlayerAdvancementTracker tracker) {
        listeners.remove(tracker);
    }

    @Override
    public Instance<T> conditionsFromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Instance<>(getId(), predicate.deserialize(obj));
    }

    public void trigger(ServerPlayerEntity player, T condition) {
        Listeners<T> list = listeners.get(player.getAdvancementTracker());

        if(list != null) {
            list.trigger(player, condition);
        }
    }

    public static class Instance<T> extends AbstractCriterionConditions {
        IGenericPredicate<T> predicate;

        Instance(Identifier location, IGenericPredicate<T> predicate) {
            super(location, EntityPredicate.Extended.EMPTY);

            this.predicate = predicate;
        }

        public boolean test(ServerPlayerEntity player, T event) {
            return predicate.test(player, event);
        }
    }

    public static class Listeners<T> {
        PlayerAdvancementTracker advancements;
        Set<ConditionsContainer<Instance<T>>> listeners = Sets.newHashSet();

        Listeners(PlayerAdvancementTracker advancements) {
            this.advancements = advancements;
        }

        public boolean isEmpty() {
            return listeners.isEmpty();
        }

        public void add(ConditionsContainer<Instance<T>> listener) {
            listeners.add(listener);
        }

        public void remove(ConditionsContainer<Instance<T>> listener) {
            listeners.remove(listener);
        }

        void trigger(ServerPlayerEntity player, T condition) {
            List<ConditionsContainer<Instance<T>>> list = Lists.newArrayList();

            for(ConditionsContainer<Instance<T>> listener: listeners) {
                if(listener.getConditions().test(player, condition)) {
                    list.add(listener);
                }
            }
            if(list.size() != 0) {
                for(ConditionsContainer<Instance<T>> listener : list) {
                    listener.grant(advancements);
                }
            }
        }
    }
}
