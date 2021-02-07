package noobanidus.mods.lootr.setup;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.advancement.ChestPredicate;
import noobanidus.mods.lootr.advancement.GenericTrigger;
import noobanidus.mods.lootr.init.ModMisc;
import noobanidus.mods.lootr.init.ModStats;

public class CommonSetup {
  public static void init(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      ModMisc.register();
      Lootr.CHEST_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.CHEST_LOCATION, new ChestPredicate()));
      Lootr.BARREL_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.BARREL_LOCATION, new ChestPredicate()));
      Lootr.CART_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(Lootr.CART_LOCATION, new ChestPredicate()));
      Registry.register(Registry.CUSTOM_STAT, ModStats.LOOTED_LOCATION, ModStats.LOOTED_LOCATION);
    });
  }
}
