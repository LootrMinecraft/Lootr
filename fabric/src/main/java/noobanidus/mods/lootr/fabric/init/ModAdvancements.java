package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import noobanidus.mods.lootr.common.advancement.AdvancementTrigger;
import noobanidus.mods.lootr.common.advancement.ContainerTrigger;
import noobanidus.mods.lootr.common.advancement.LootedStatTrigger;
import noobanidus.mods.lootr.common.api.LootrAPI;

public class ModAdvancements {
  public static final Identifier CHEST_LOCATION = LootrAPI.rl("chest_opened");
  public static final Identifier BARREL_LOCATION = LootrAPI.rl("barrel_opened");
  public static final Identifier CART_LOCATION = LootrAPI.rl("cart_opened");
  public static final Identifier SHULKER_LOCATION = LootrAPI.rl("shulker_opened");
  public static final Identifier ADVANCEMENT_LOCATION = LootrAPI.rl("advancement");
  public static final Identifier SCORE_LOCATION = LootrAPI.rl("score");
  public static final Identifier GRAVEL_LOCATION = LootrAPI.rl("gravel_brushed");
  public static final Identifier SAND_LOCATION = LootrAPI.rl("sand_brushed");
  public static final Identifier POT_OPENED = LootrAPI.rl("pot_opened");
  public static final Identifier ITEM_FRAME_LOCATION = LootrAPI.rl("item_frame_looted");
  public static ContainerTrigger CHEST = null;
  public static ContainerTrigger BARREL = null;
  public static ContainerTrigger CART = null;
  public static ContainerTrigger SHULKER = null;
  public static ContainerTrigger GRAVEL = null;
  public static ContainerTrigger SAND = null;
  public static ContainerTrigger POT = null;
  public static LootedStatTrigger SCORE = null;
  public static AdvancementTrigger ADVANCEMENT = null;
  public static ContainerTrigger ITEM_FRAME = null;

  public static void registerAdvancements() {
    ADVANCEMENT = Registry.register(BuiltInRegistries.TRIGGER_TYPES, ADVANCEMENT_LOCATION, new AdvancementTrigger());
    CHEST = Registry.register(BuiltInRegistries.TRIGGER_TYPES, CHEST_LOCATION, new ContainerTrigger());
    BARREL = Registry.register(BuiltInRegistries.TRIGGER_TYPES, BARREL_LOCATION, new ContainerTrigger());
    CART = Registry.register(BuiltInRegistries.TRIGGER_TYPES, CART_LOCATION, new ContainerTrigger());
    SHULKER = Registry.register(BuiltInRegistries.TRIGGER_TYPES, SHULKER_LOCATION, new ContainerTrigger());
    SCORE = Registry.register(BuiltInRegistries.TRIGGER_TYPES, SCORE_LOCATION, new LootedStatTrigger());
    GRAVEL = Registry.register(BuiltInRegistries.TRIGGER_TYPES, GRAVEL_LOCATION, new ContainerTrigger());
    SAND = Registry.register(BuiltInRegistries.TRIGGER_TYPES, SAND_LOCATION, new ContainerTrigger());
    POT = Registry.register(BuiltInRegistries.TRIGGER_TYPES, POT_OPENED, new ContainerTrigger());
    ITEM_FRAME = Registry.register(BuiltInRegistries.TRIGGER_TYPES, ITEM_FRAME_LOCATION, new ContainerTrigger());
  }
}
