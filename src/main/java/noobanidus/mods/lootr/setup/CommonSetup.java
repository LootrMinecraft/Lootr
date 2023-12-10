package noobanidus.mods.lootr.setup;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.lootr.advancement.AdvancementPredicate;
import noobanidus.mods.lootr.advancement.ContainerPredicate;
import noobanidus.mods.lootr.advancement.LootedStatPredicate;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.impl.LootrAPIImpl;
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
      /*
      ModAdvancements.CHEST_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.CHEST_LOCATION, new ContainerPredicate()));
      ModAdvancements.BARREL_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.BARREL_LOCATION, new ContainerPredicate()));
      ModAdvancements.CART_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.CART_LOCATION, new ContainerPredicate()));
      ModAdvancements.SHULKER_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.SHULKER_LOCATION, new ContainerPredicate()));
      ModAdvancements.ADVANCEMENT_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.ADVANCEMENT_LOCATION, new AdvancementPredicate()));

       */
      ModStats.load();
      //ModAdvancements.SCORE_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.SCORE_LOCATION, new LootedStatPredicate()));
      PacketHandler.registerMessages();
    });
  }
}
