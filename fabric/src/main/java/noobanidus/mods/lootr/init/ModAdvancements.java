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
  public static ContainerTrigger CHEST = null;
  public static ContainerTrigger BARREL = null;
  public static ContainerTrigger CART = null;
  public static ContainerTrigger SHULKER = null;
  public static LootedStatTrigger SCORE = null;
  public static AdvancementTrigger ADVANCEMENT = null;

  public static void registerAdvancements() {
    ADVANCEMENT = Registry.register(BuiltInRegistries.TRIGGER_TYPES, ADVANCEMENT_LOCATION, new AdvancementTrigger());
    CHEST = Registry.register(BuiltInRegistries.TRIGGER_TYPES, CHEST_LOCATION, new ContainerTrigger());
    BARREL = Registry.register(BuiltInRegistries.TRIGGER_TYPES, BARREL_LOCATION, new ContainerTrigger());
    CART = Registry.register(BuiltInRegistries.TRIGGER_TYPES, CART_LOCATION, new ContainerTrigger());
    SHULKER = Registry.register(BuiltInRegistries.TRIGGER_TYPES, SHULKER_LOCATION, new ContainerTrigger());
    SCORE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, SCORE_LOCATION, new LootedStatTrigger());
  }
}
