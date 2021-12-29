package noobanidus.mods.lootr.setup;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.advancement.*;
import noobanidus.mods.lootr.api.LootrHooks;
import noobanidus.mods.lootr.impl.LootrHooksImpl;
import noobanidus.mods.lootr.init.ModLoot;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.networking.PacketHandler;

public class CommonSetup {
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
