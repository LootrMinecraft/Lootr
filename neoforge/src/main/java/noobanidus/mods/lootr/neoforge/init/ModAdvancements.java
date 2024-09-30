package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.advancement.AdvancementTrigger;
import noobanidus.mods.lootr.common.advancement.ContainerTrigger;
import noobanidus.mods.lootr.common.advancement.LootedStatTrigger;
import noobanidus.mods.lootr.common.api.LootrAPI;

public class ModAdvancements {
  private static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, LootrAPI.MODID);

  public static final DeferredHolder<CriterionTrigger<?>, AdvancementTrigger> ADVANCEMENT = REGISTER.register("advancement", AdvancementTrigger::new);
  public static final DeferredHolder<CriterionTrigger<?>, ContainerTrigger> CHEST = REGISTER.register("chest_opened", ContainerTrigger::new);
  public static final DeferredHolder<CriterionTrigger<?>, ContainerTrigger> BARREL = REGISTER.register("barrel_opened", ContainerTrigger::new);
  public static final DeferredHolder<CriterionTrigger<?>, ContainerTrigger> CART = REGISTER.register("cart_opened", ContainerTrigger::new);
  public static final DeferredHolder<CriterionTrigger<?>, ContainerTrigger> SHULKER = REGISTER.register("shulker_opened", ContainerTrigger::new);
  public static final DeferredHolder<CriterionTrigger<?>, LootedStatTrigger> STAT = REGISTER.register("score", LootedStatTrigger::new);


  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
