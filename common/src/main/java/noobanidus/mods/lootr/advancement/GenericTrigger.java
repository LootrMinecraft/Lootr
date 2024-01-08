package noobanidus.mods.lootr.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.api.advancement.IGenericPredicate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericTrigger<T> implements CriterionTrigger<GenericTrigger.Instance<T>> {
    private final ResourceLocation id;
    private final Map<PlayerAdvancements, Listeners<T>> listeners = Maps.newHashMap();
    private final IGenericPredicate<T> predicate;

    public GenericTrigger(String id, IGenericPredicate<T> predicate) {
        this(new ResourceLocation(id), predicate);
    }

    public GenericTrigger(ResourceLocation id, IGenericPredicate<T> predicate) { //}, IGenericPredicate basePredicate) {
        this.id = id;
        this.predicate = predicate;
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void addPlayerListener(@NotNull PlayerAdvancements advancementsIn, @NotNull Listener<Instance<T>> listener) {
        Listeners<T> list = listeners.get(advancementsIn);

        if (list == null) {
            list = new Listeners<>(advancementsIn);
            listeners.put(advancementsIn, list);
        }

        list.add(listener);
    }

    @Override
    public void removePlayerListener(@NotNull PlayerAdvancements advancementsIn, @NotNull Listener<Instance<T>> listener) {
        Listeners<T> list = listeners.get(advancementsIn);

        if (list != null) {
            list.remove(listener);

            if (list.isEmpty()) {
                listeners.remove(advancementsIn);
            }
        }
    }

    @Override
    public void removePlayerListeners(@NotNull PlayerAdvancements advancementsIn) {
        listeners.remove(advancementsIn);
    }

    @Override
    public Instance<T> createInstance(JsonObject jsonObject, DeserializationContext conditionArrayParser) {
        ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(jsonObject, "player", conditionArrayParser);
        return this.createInstance(jsonObject, contextawarepredicate, conditionArrayParser);
    }

    public Instance<T> createInstance(JsonObject jsonObject, ContextAwarePredicate context, DeserializationContext conditionArrayParser) {
        return new Instance<>(getId(), context, predicate.deserialize(jsonObject));
    }

    public void trigger(ServerPlayer player, T condition) {
        Listeners<T> list = listeners.get(player.getAdvancements());

        if (list != null) {
            list.trigger(player, condition);
        }
    }

    public static class Instance<T> extends AbstractCriterionTriggerInstance {
        IGenericPredicate<T> predicate;

        Instance(ResourceLocation location, ContextAwarePredicate contextPredicate, IGenericPredicate<T> predicate) {
            super(location, contextPredicate);

            this.predicate = predicate;
        }

        public boolean test(ServerPlayer player, T event) {
            return predicate.test(player, event);
        }
    }

    public static class Listeners<T> {
        PlayerAdvancements advancements;
        Set<Listener<Instance<T>>> listeners = Sets.newHashSet();

        Listeners(PlayerAdvancements advancementsIn) {
            this.advancements = advancementsIn;
        }

        public boolean isEmpty() {
            return listeners.isEmpty();
        }

        public void add(Listener<Instance<T>> listener) {
            listeners.add(listener);
        }

        public void remove(Listener<Instance<T>> listener) {
            listeners.remove(listener);
        }

        void trigger(ServerPlayer player, T condition) {
            List<Listener<Instance<T>>> list = Lists.newArrayList();

            for (Listener<Instance<T>> listener : listeners) {
                if (listener.getTriggerInstance().test(player, condition)) {
                    list.add(listener);
                }
            }

            if (list.size() != 0) {
                for (Listener<Instance<T>> listener : list) {
                    listener.run(advancements);
                }
            }
        }
    }
}