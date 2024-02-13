package net.zestyblaze.lootr.registry;

import net.minecraft.advancements.CriteriaTriggers;
import net.zestyblaze.lootr.advancement.AdvancementTrigger;
import net.zestyblaze.lootr.advancement.ContainerTrigger;
import net.zestyblaze.lootr.advancement.LootedStatTrigger;

public class LootrAdvancementsInit {
  public static AdvancementTrigger ADVANCEMENT_PREDICATE = CriteriaTriggers.register("advancement_predicate", new AdvancementTrigger());
  public static ContainerTrigger CHEST_PREDICATE = CriteriaTriggers.register("chest_predicate", new ContainerTrigger());
  public static ContainerTrigger BARREL_PREDICATE = CriteriaTriggers.register("barrel_predicate", new ContainerTrigger());
  public static ContainerTrigger CART_PREDICATE = CriteriaTriggers.register("cart_predicate", new ContainerTrigger());
  public static ContainerTrigger SHULKER_PREDICATE = CriteriaTriggers.register("shulker_predicate", new ContainerTrigger());
  public static LootedStatTrigger SCORE_PREDICATE = CriteriaTriggers.register("score_predicate", new LootedStatTrigger());

  public static void registerAdvancements() {
  }
}
