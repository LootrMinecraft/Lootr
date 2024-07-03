package noobanidus.mods.lootr.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.advancement.AdvancementTrigger;
import noobanidus.mods.lootr.advancement.ContainerTrigger;
import noobanidus.mods.lootr.advancement.LootedStatTrigger;
import noobanidus.mods.lootr.api.LootrAPI;

public class ModAdvancements {
  public static final ResourceLocation CHEST_LOCATION = LootrAPI.rl("chest_opened");
  public static final ResourceLocation BARREL_LOCATION = LootrAPI.rl("barrel_opened");
  public static final ResourceLocation CART_LOCATION = LootrAPI.rl("cart_opened");
  public static final ResourceLocation SHULKER_LOCATION = LootrAPI.rl("shulker_opened");
  public static final ResourceLocation ADVANCEMENT_LOCATION = LootrAPI.rl("advancement");
  public static final ResourceLocation SCORE_LOCATION = LootrAPI.rl("score");
  public static ContainerTrigger CHEST_PREDICATE = null;
  public static ContainerTrigger BARREL_PREDICATE = null;
  public static ContainerTrigger CART_PREDICATE = null;
  public static ContainerTrigger SHULKER_PREDICATE = null;
  public static LootedStatTrigger SCORE_PREDICATE = null;
  public static AdvancementTrigger ADVANCEMENT_PREDICATE = null;

  public static void registerAdvancements() {
    ADVANCEMENT_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, ADVANCEMENT_LOCATION, new AdvancementTrigger());
    CHEST_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, CHEST_LOCATION, new ContainerTrigger());
    BARREL_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, BARREL_LOCATION, new ContainerTrigger());
    CART_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, CART_LOCATION, new ContainerTrigger());
    SHULKER_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, SHULKER_LOCATION, new ContainerTrigger());
    SCORE_PREDICATE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, SCORE_LOCATION, new LootedStatTrigger());
  }
}
