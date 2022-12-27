package noobanidus.mods.lootr.setup;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.lootr.advancement.AdvancementPredicate;
import noobanidus.mods.lootr.advancement.ContainerPredicate;
import noobanidus.mods.lootr.advancement.GenericTrigger;
import noobanidus.mods.lootr.advancement.LootedStatPredicate;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.LootrHooks;
import noobanidus.mods.lootr.impl.LootrAPIImpl;
import noobanidus.mods.lootr.impl.LootrHooksImpl;
import noobanidus.mods.lootr.init.ModAdvancements;
import noobanidus.mods.lootr.init.ModLoot;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.network.PacketHandler;

@Mod.EventBusSubscriber(modid= LootrAPI.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
  @SubscribeEvent
  public static void init(FMLCommonSetupEvent event) {
    LootrAPI.INSTANCE = new LootrAPIImpl();

    event.enqueueWork(() -> {
      ModLoot.register();
      ModAdvancements.CHEST_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.CHEST_LOCATION, new ContainerPredicate()));
      ModAdvancements.BARREL_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.BARREL_LOCATION, new ContainerPredicate()));
      ModAdvancements.CART_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.CART_LOCATION, new ContainerPredicate()));
      ModAdvancements.SHULKER_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.SHULKER_LOCATION, new ContainerPredicate()));
      ModAdvancements.ADVANCEMENT_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.ADVANCEMENT_LOCATION, new AdvancementPredicate()));
      Registry.register(BuiltInRegistries.CUSTOM_STAT, ModStats.LOOTED_LOCATION, ModStats.LOOTED_LOCATION);
      ModStats.load();
      ModAdvancements.SCORE_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.SCORE_LOCATION, new LootedStatPredicate()));
      PacketHandler.registerMessages();
    });
  }
}
