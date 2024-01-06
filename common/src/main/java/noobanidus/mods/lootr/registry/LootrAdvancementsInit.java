package noobanidus.mods.lootr.registry;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.advancement.AdvancementPredicate;
import noobanidus.mods.lootr.advancement.ContainerPredicate;
import noobanidus.mods.lootr.advancement.GenericTrigger;
import noobanidus.mods.lootr.advancement.LootedStatPredicate;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.UUID;

public class LootrAdvancementsInit {
    public static final ResourceLocation CHEST_LOCATION = new ResourceLocation(LootrAPI.MODID, "chest_opened");
    public static final ResourceLocation BARREL_LOCATION = new ResourceLocation(LootrAPI.MODID, "barrel_opened");
    public static final ResourceLocation CART_LOCATION = new ResourceLocation(LootrAPI.MODID, "cart_opened");
    public static final ResourceLocation SHULKER_LOCATION = new ResourceLocation(LootrAPI.MODID, "shulker_opened");
    public static final ResourceLocation ADVANCEMENT_LOCATION = new ResourceLocation(LootrAPI.MODID, "advancement");
    public static final ResourceLocation SCORE_LOCATION = new ResourceLocation(LootrAPI.MODID, "score");
    public static GenericTrigger<UUID> CHEST_PREDICATE = null;
    public static GenericTrigger<UUID> BARREL_PREDICATE = null;
    public static GenericTrigger<UUID> CART_PREDICATE = null;
    public static GenericTrigger<UUID> SHULKER_PREDICATE = null;
    public static GenericTrigger<Void> SCORE_PREDICATE = null;
    public static GenericTrigger<ResourceLocation> ADVANCEMENT_PREDICATE = null;

    public static void registerAdvancements () {
        CHEST_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(CHEST_LOCATION, new ContainerPredicate()));
        BARREL_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(BARREL_LOCATION, new ContainerPredicate()));
        CART_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(CART_LOCATION, new ContainerPredicate()));
        SHULKER_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(SHULKER_LOCATION, new ContainerPredicate()));
        ADVANCEMENT_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ADVANCEMENT_LOCATION, new AdvancementPredicate()));
        SCORE_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(SCORE_LOCATION, new LootedStatPredicate()));
    }
}
