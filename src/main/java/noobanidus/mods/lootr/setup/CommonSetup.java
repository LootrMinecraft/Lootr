package noobanidus.mods.lootr.setup;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.advancement.AdvancementPredicate;
import noobanidus.mods.lootr.advancement.ContainerPredicate;
import noobanidus.mods.lootr.advancement.GenericTrigger;
import noobanidus.mods.lootr.advancement.LootedStatPredicate;
import noobanidus.mods.lootr.api.LootrHooks;
import noobanidus.mods.lootr.impl.LootrHooksImpl;
import noobanidus.mods.lootr.init.ModLoot;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.networking.PacketHandler;

@Mod.EventBusSubscriber(modid=Lootr.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
  @SubscribeEvent
  public static void init(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      ModLoot.register();
      Lootr.CHEST_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.CHEST_LOCATION, new ContainerPredicate()));
      Lootr.BARREL_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.BARREL_LOCATION, new ContainerPredicate()));
      Lootr.CART_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.CART_LOCATION, new ContainerPredicate()));
      Lootr.SHULKER_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.SHULKER_LOCATION, new ContainerPredicate()));
      Lootr.ADVANCEMENT_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.ADVANCEMENT_LOCATION, new AdvancementPredicate()));
      Registry.register(Registry.CUSTOM_STAT, ModStats.LOOTED_LOCATION, ModStats.LOOTED_LOCATION);
      ModStats.load();
      Lootr.SCORE_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.SCORE_LOCATION, new LootedStatPredicate()));
      PacketHandler.registerMessages();
      LootrHooks.INSTANCE = new LootrHooksImpl();
    });
  }
}
