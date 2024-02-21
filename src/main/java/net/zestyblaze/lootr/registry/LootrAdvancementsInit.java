package net.zestyblaze.lootr.registry;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.zestyblaze.lootr.advancement.AdvancementTrigger;
import net.zestyblaze.lootr.advancement.ContainerTrigger;
import net.zestyblaze.lootr.advancement.LootedStatTrigger;
import net.zestyblaze.lootr.api.LootrAPI;

public class LootrAdvancementsInit {
  public static AdvancementTrigger ADVANCEMENT_PREDICATE;
  public static ContainerTrigger CHEST_PREDICATE;
  public static ContainerTrigger BARREL_PREDICATE;
  public static ContainerTrigger CART_PREDICATE;
  public static ContainerTrigger SHULKER_PREDICATE;
  public static LootedStatTrigger SCORE_PREDICATE;

  public static void registerAdvancements() {
    ADVANCEMENT_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, new ResourceLocation(LootrAPI.MODID, "advancement"), new AdvancementTrigger());
    CHEST_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, new ResourceLocation(LootrAPI.MODID, "chest_opened"), new ContainerTrigger());
    BARREL_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, new ResourceLocation(LootrAPI.MODID, "barrel_opened"), new ContainerTrigger());
    CART_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, new ResourceLocation(LootrAPI.MODID, "cart_opened"), new ContainerTrigger());
    SHULKER_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, new ResourceLocation(LootrAPI.MODID, "shulker_opened"), new ContainerTrigger());
    SCORE_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, new ResourceLocation(LootrAPI.MODID, "score"), new LootedStatTrigger());
  }
}
