package noobanidus.mods.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.conditions.LootCount;

import java.util.function.Supplier;

public class LootrLootInit {
    private static LootItemConditionType lootCount;
    public static final Supplier<LootItemConditionType> LOOT_COUNT_PROVIDER = () -> lootCount;

    public static void registerLoot() {
        lootCount = new LootItemConditionType(new LootCount.Serializer());
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation(LootrAPI.MODID, "loot_count"), lootCount);
    }
}
