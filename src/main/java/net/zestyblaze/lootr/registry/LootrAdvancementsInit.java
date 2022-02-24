package net.zestyblaze.lootr.registry;

import net.minecraft.util.Identifier;
import net.zestyblaze.lootr.Lootr;
import net.zestyblaze.lootr.advancement.GenericTrigger;
import net.zestyblaze.lootr.config.LootrModConfig;

import java.util.UUID;

public class LootrAdvancementsInit {
    public static final Identifier CHEST_LOCATION = new Identifier(Lootr.MODID, "chest_opened");
    public static final Identifier BARREL_LOCATION = new Identifier(Lootr.MODID, "barrel_opened");
    public static final Identifier CART_LOCATION = new Identifier(Lootr.MODID, "cart_opened");
    public static final Identifier SHULKER_LOCATION = new Identifier(Lootr.MODID, "shulker_opened");
    public static final Identifier ADVANCEMENT_LOCATION = new Identifier(Lootr.MODID, "advancement");
    public static final Identifier SCORE_LOCATION = new Identifier(Lootr.MODID, "score");
    public static GenericTrigger<UUID> CHEST_PREDICATE = null;
    public static GenericTrigger<UUID> BARREL_PREDICATE = null;
    public static GenericTrigger<UUID> CART_PREDICATE = null;
    public static GenericTrigger<UUID> SHULKER_PREDICATE = null;
    public static GenericTrigger<Void> SCORE_PREDICATE = null;
    public static GenericTrigger<Identifier> ADVANCEMENT_PREDICATE = null;

    public static void load() {
        if(LootrModConfig.get().debugMode) {
            Lootr.LOGGER.info("Lootr: Registry - Advancements Loaded!");
        }
    }
}
